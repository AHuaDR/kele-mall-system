<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  nickname: userStore.userInfo.nickname,
  gender: '男',
  birthday: '2000-01-01',
  intro: '这个人很懒，什么都没写'
})

// --- 头像上传逻辑 ---
const onReadAvatar = (file: any) => {
  // file.content 是图片的 Base64 编码，可以直接用于显示
  userStore.userInfo.avatar = file.content
  showToast('头像更新成功')
}

// --- 性别/生日逻辑 (保持不变) ---
const showGender = ref(false)
const genderColumns = [{ text: '男', value: '男' }, { text: '女', value: '女' }]
const onConfirmGender = ({ selectedOptions }: any) => {
  form.value.gender = selectedOptions[0].text
  showGender.value = false
}

const showBirthday = ref(false)
const currentDate = ref(form.value.birthday.split('-')) 
const minDate = new Date(1950, 0, 1)
const maxDate = new Date()
const onConfirmBirthday = ({ selectedValues }: any) => {
  form.value.birthday = selectedValues.join('-')
  showBirthday.value = false
}

const onSave = () => {
  userStore.userInfo.nickname = form.value.nickname
  showToast('保存成功')
  router.back()
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="个人资料" left-arrow @click-left="router.back()" right-text="保存" @click-right="onSave" fixed placeholder />
    
    <div class="mt-3">
      <van-cell-group inset>
        <van-cell title="头像" center>
           <template #value>
              <van-uploader :after-read="onReadAvatar">
                <van-image round width="50" height="50" :src="userStore.userInfo.avatar" class="block" />
              </van-uploader>
           </template>
        </van-cell>
        
        <van-field v-model="form.nickname" label="昵称" input-align="right" placeholder="请输入昵称" />
        <van-cell title="性别" is-link :value="form.gender" @click="showGender = true" />
        <van-cell title="生日" is-link :value="form.birthday" @click="showBirthday = true" />
        <van-field v-model="form.intro" label="个性签名" input-align="right" type="textarea" rows="2" autosize maxlength="50" show-word-limit />
      </van-cell-group>
    </div>

    <van-popup v-model:show="showGender" round position="bottom">
      <van-picker :columns="genderColumns" @cancel="showGender = false" @confirm="onConfirmGender" />
    </van-popup>

    <van-popup v-model:show="showBirthday" round position="bottom">
      <van-date-picker v-model="currentDate" title="选择生日" :min-date="minDate" :max-date="maxDate" @cancel="showBirthday = false" @confirm="onConfirmBirthday" />
    </van-popup>
  </div>
</template>