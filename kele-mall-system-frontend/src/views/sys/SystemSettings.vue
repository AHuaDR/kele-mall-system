<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { User, Lock, Edit, Plus, Delete, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

const activeTab = ref('users')

// ==========================================
// 1. 管理员账号管理 (Admin)
// ==========================================
const users = ref<any[]>([])
const loadingUsers = ref(false)
const userTotal = ref(0) 

// 查询参数
const userParams = reactive({
  keyword: '',
  pageNum: 1,  
  pageSize: 10 
})

const userDialog = ref(false)
const userForm = reactive({ username: '', password: '', email: '', nickName: '', roleId: '' })
const roleOptions = ref<any[]>([])

// 获取管理员列表
const loadUsers = () => {
  loadingUsers.value = true
  doGet('/admin/list', userParams).then((res: any) => {
    if (res.code === 200 && res.data) {
      const pageData = res.data || {}
      users.value = pageData.list || []
      userTotal.value = pageData.total || 0
    }
    loadingUsers.value = false
  }).catch(() => {
    loadingUsers.value = false
  })
}

const handleSearch = () => {
  userParams.pageNum = 1 
  loadUsers()
}

const handleSizeChange = (val: number) => {
  userParams.pageSize = val
  loadUsers()
}

const handlePageChange = (val: number) => {
  userParams.pageNum = val
  loadUsers()
}

// 获取角色列表
const loadRoles = () => {
  doGet('/admin/role/listAll').then((res: any) => {
    if (res.data) {
      roles.value = res.data
      roleOptions.value = res.data
    }
  })
}

// 打开添加用户弹窗
const openAddUser = () => {
  userForm.username = ''
  userForm.password = ''
  userForm.email = ''
  userForm.nickName = ''
  userForm.roleId = ''
  userDialog.value = true
}

// 提交添加用户
const submitUser = async () => {
  if(!userForm.username) return ElMessage.warning('请输入用户名')
  
  const res: any = await doPost('/admin/register', {
    username: userForm.username,
    password: userForm.password || '123456',
    email: userForm.email,
    nickName: userForm.nickName,
    note: '后台添加'
  })

  if (res.code === 200) {
    if (userForm.roleId) {
      const newUserId = res.data?.id || res.data
      if (newUserId) {
        await doPost('/admin/role/update', {
          adminId: newUserId,
          roleIds: [userForm.roleId]
        })
      }
    }
    ElMessage.success('管理员添加成功')
    userDialog.value = false
    loadUsers()
  }
}

const handleStatusChange = (row: any) => {
  const statusVal = row.status === 1 ? 1 : 0
  doPost(`/admin/updateStatus/${row.id}?status=${statusVal}`).then(() => {
    ElMessage.success('状态已更新')
  })
}

const handleDeleteUser = (row: any) => {
  ElMessageBox.confirm(`确认删除用户 ${row.username}?`, '警告', { type: 'warning' })
    .then(() => { 
       doPost(`/admin/delete/${row.id}`).then(() => {
         ElMessage.success('已删除')
         loadUsers()
       })
    })
}

// ==========================================
// 2. 角色管理 (Role)
// ==========================================
const roles = ref<any[]>([])
const roleDialog = ref(false)
const isRoleEdit = ref(false) 
// [修改] 增加 status 字段
const roleForm = reactive({ id: null, name: '', description: '', status: 1 })

// 打开【新增角色】弹窗
const openAddRole = () => { 
  isRoleEdit.value = false
  roleForm.id = null
  roleForm.name = ''
  roleForm.description = ''
  roleForm.status = 1 // [新增] 默认启用
  roleDialog.value = true 
}

// 打开【编辑角色】弹窗
const openUpdateRole = (row: any) => {
  isRoleEdit.value = true
  roleForm.id = row.id
  roleForm.name = row.name
  roleForm.description = row.description
  roleForm.status = row.status // [新增] 回显状态
  roleDialog.value = true
}

// 提交角色
const submitRole = async () => {
  if (!roleForm.name) return ElMessage.warning('请输入角色名称')

  if (isRoleEdit.value) {
    // [修改] 提交包含 status
    await doPost(`/admin/role/update/${roleForm.id}`, { 
      name: roleForm.name, 
      description: roleForm.description,
      status: roleForm.status
    })
    ElMessage.success('更新成功')
  } else {
    // [修改] 提交包含 status
    await doPost('/admin/role/create', { 
      name: roleForm.name, 
      description: roleForm.description, 
      status: roleForm.status 
    })
    ElMessage.success('创建成功')
  }
  
  roleDialog.value = false
  loadRoles()
}

// ==========================================
// 3. 权限配置 (Menu/Permission)
// ==========================================
const permDialog = ref(false)
const currentRole: any = ref({})
const treeRef = ref()
const permTreeData = ref<any[]>([]) 

const listToTree = (list: any[], parentId: number = 0) => {
  const tree: any[] = []
  list.forEach(item => {
    if (item.parentId === parentId) {
      const children = listToTree(list, item.id)
      if (children.length > 0) item.children = children
      tree.push(item)
    }
  })
  return tree
}

const getLeafIds = (tree: any[], ownedMenuIds: number[]) => {
  const leafIds: number[] = []
  const traverse = (nodes: any[]) => {
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      } else {
        if (ownedMenuIds.includes(node.id)) leafIds.push(node.id)
      }
    })
  }
  traverse(tree)
  return leafIds
}

const loadMenuTree = () => {
  doGet('/admin/menu/treeList').then((res: any) => {
    if (res.data) permTreeData.value = listToTree(res.data, 0)
  })
}

// 打开权限分配弹窗
const openPermDialog = async (role: any) => {
  currentRole.value = role
  permDialog.value = true
  
  if (permTreeData.value.length === 0) {
    loadMenuTree()
  }

  const res: any = await doGet(`/admin/role/listMenu/${role.id}`)
  const checkedIds = res.data ? res.data.map((m: any) => m.menuId) : []
  const leafIds = getLeafIds(permTreeData.value, checkedIds)

  setTimeout(() => {
    treeRef.value!.setCheckedKeys(leafIds, false)
  }, 100)
}

const submitPerm = () => {
  const checkedKeys = treeRef.value!.getCheckedKeys()
  const halfCheckedKeys = treeRef.value!.getHalfCheckedKeys()
  const allKeys = [...checkedKeys, ...halfCheckedKeys]

  doPost('/admin/role/allocMenu', {
    roleId: currentRole.value.id,
    menuIds: allKeys
  }).then(() => {
    ElMessage.success(`角色 [${currentRole.value.name}] 权限已更新`)
    permDialog.value = false
  })
}

// 初始化
onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)]">
    <h2 class="text-2xl font-bold text-white mb-6">系统权限设置</h2>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="账号管理" name="users">
        
        <div class="flex justify-between mb-4">
           <div class="flex gap-2">
             <el-input 
               v-model="userParams.keyword" 
               placeholder="搜索用户名/昵称" 
               class="w-60" 
               clearable
               :prefix-icon="Search"
               @keyup.enter="handleSearch"
               @clear="handleSearch"
             />
             <el-button type="primary" color="#3D5A80" @click="handleSearch">查询</el-button>
           </div>
           <el-button type="primary" :icon="Plus" color="#3D5A80" @click="openAddUser">添加管理员</el-button>
        </div>

        <el-table :data="users" v-loading="loadingUsers" style="width: 100%">
           <el-table-column label="管理员" width="200">
             <template #default="{ row }">
               <div class="flex items-center gap-3">
                 <el-avatar :size="32" class="bg-accent-100" :src="row.icon">{{ (row.nickName || row.username || 'A').substring(0,1) }}</el-avatar>
                 <div>
                    <div class="text-text-100">{{ row.nickName || row.username }}</div>
                    <div class="text-xs text-text-200">{{ row.email }}</div>
                 </div>
               </div>
             </template>
           </el-table-column>
           <el-table-column prop="loginTime" label="最后登录">
              <template #default="{ row }">
                 {{ row.loginTime ? row.loginTime.substring(0, 16) : '从未登录' }}
              </template>
           </el-table-column>
           <el-table-column label="启用状态">
             <template #default="{ row }">
               <el-switch 
                 v-model="row.status" 
                 :active-value="1" 
                 :inactive-value="0"
                 active-color="#3D5A80" 
                 inactive-color="#374357" 
                 @change="handleStatusChange(row)"
               />
             </template>
           </el-table-column>
           <el-table-column label="操作">
             <template #default="{ row }">
               <el-button link type="danger" @click="handleDeleteUser(row)">删除</el-button>
             </template>
           </el-table-column>
        </el-table>

        <div class="flex justify-end mt-4 pt-4 border-t border-bg-100">
          <el-pagination
            v-model:current-page="userParams.pageNum"
            v-model:page-size="userParams.pageSize"
            :page-sizes="[10, 20, 50]"
            :background="true"
            layout="total, sizes, prev, pager, next, jumper"
            :total="userTotal"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="角色权限" name="roles">
        <div class="flex justify-end mb-4">
           <el-button type="primary" :icon="Plus" color="#3D5A80" @click="openAddRole">新建角色</el-button>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
           <div v-for="role in roles" :key="role.id" class="bg-bg-300 p-6 rounded border border-bg-300 hover:border-accent-100 transition-colors">
              <div class="flex justify-between items-center mb-4">
                 <div class="bg-accent-100/20 text-accent-200 p-2 rounded"><el-icon><Lock /></el-icon></div>
                 
                 <el-button circle :icon="Edit" size="small" @click.stop="openUpdateRole(role)" />
                 
              </div>
              <h3 class="text-lg font-bold text-white">{{ role.name }}</h3>
              <p class="text-text-200 text-sm h-10">{{ role.description || '暂无描述' }}</p>
              <div class="mt-4 pt-4 border-t border-bg-200 text-xs text-text-200 flex justify-between">
                 <span>状态: {{ role.status === 1 ? '启用' : '禁用' }}</span>
                 
                 <span class="text-accent-200 cursor-pointer hover:underline" @click.stop="openPermDialog(role)">配置权限 ></span>
                 
              </div>
           </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="roleDialog" :title="isRoleEdit ? '编辑角色信息' : '新建角色'" width="400px">
       <el-form label-position="top">
         <el-form-item label="角色名称" required>
            <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
         </el-form-item>
         <el-form-item label="职责描述">
            <el-input v-model="roleForm.description" type="textarea" placeholder="请输入角色描述" />
         </el-form-item>
         
         <el-form-item label="启用状态">
            <el-switch 
              v-model="roleForm.status" 
              :active-value="1" 
              :inactive-value="0"
              active-text="启用" 
              inactive-text="禁用" 
            />
         </el-form-item>
       </el-form>
       <template #footer>
         <el-button @click="roleDialog = false">取消</el-button>
         <el-button type="primary" color="#3D5A80" @click="submitRole">确定</el-button>
       </template>
    </el-dialog>

    <el-dialog v-model="userDialog" title="添加管理员" width="400px">
       <el-form label-position="top">
         <el-form-item label="登录账号" required><el-input v-model="userForm.username" placeholder="请输入用户名" /></el-form-item>
         <el-form-item label="真实姓名"><el-input v-model="userForm.nickName" placeholder="显示名称" /></el-form-item>
         <el-form-item label="邮箱地址"><el-input v-model="userForm.email" /></el-form-item>
         <el-form-item label="初始密码"><el-input v-model="userForm.password" placeholder="默认: 123456" show-password /></el-form-item>
         <el-form-item label="分配角色">
            <el-select v-model="userForm.roleId" class="w-full" placeholder="请选择角色">
               <el-option 
                 v-for="role in roleOptions" 
                 :key="role.id" 
                 :label="role.name" 
                 :value="role.id"
               />
            </el-select>
         </el-form-item>
       </el-form>
       <template #footer>
         <el-button @click="userDialog = false">取消</el-button>
         <el-button type="primary" color="#3D5A80" @click="submitUser">确定</el-button>
       </template>
    </el-dialog>

    <el-dialog v-model="permDialog" :title="`配置权限 - ${currentRole.name}`" width="500px">
       <div class="mb-4 text-text-200 text-sm">请勾选该角色可访问的后台菜单：</div>
       <div class="bg-bg-300 p-4 rounded border border-bg-100 max-h-[300px] overflow-y-auto">
          <el-tree
            ref="treeRef"
            :data="permTreeData"
            show-checkbox
            node-key="id"
            :props="{ label: 'title', children: 'children' }" 
            class="dark-tree"
          />
       </div>
       <template #footer>
         <el-button @click="permDialog = false">取消</el-button>
         <el-button type="primary" color="#3D5A80" @click="submitPerm">保存配置</el-button>
       </template>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.el-pagination__total), :deep(.el-pagination__jump) {
  color: #a0aec0; 
}
:deep(.el-tree) {
  background-color: transparent;
  color: #e0e0e0;
}
:deep(.el-tree-node__content:hover) {
  background-color: #1f2b3e;
}
:deep(.el-tree-node:focus > .el-tree-node__content) {
  background-color: #1f2b3e;
}
:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #3D5A80;
  border-color: #3D5A80;
}
</style>