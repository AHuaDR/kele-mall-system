package com.my.controller;

import com.github.pagehelper.PageInfo;
import com.my.dto.AdminInfoParam;
import com.my.dto.LoginParam;
import com.my.dto.UpdatePasswordParam;
import com.my.mapper.SysUserMapper;
import com.my.model.SysMenu;
import com.my.model.SysUser;
import com.my.query.AdminPageInfoQuery;
import com.my.result.R;
import com.my.security.AdminLoginUser;
import com.my.service.MenuService;
import com.my.service.SysUserService;
import com.my.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 14:44
 * @description
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MenuService menuService;

    /**
     * 登录操作
     * @param loginParam
     * @return
     */
    @PostMapping("/sso/login")
    public R login(@RequestBody LoginParam loginParam){
        SysUser sysUser = sysUserMapper.selectByLogin(loginParam.getUsername());
        if(sysUser == null){
            return R.error("账号不存在");
        }
        if(!passwordEncoder.matches(loginParam.getPassword(),sysUser.getPassword())){
            return R.error("密码错误");
        }
        if (sysUser.getStatus() == 0) return R.error("账号已禁用");

        String token = jwtTokenUtil.generateToken(loginParam.getUsername());

        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("tokenHead","Bearer ");

        //设置更新最近登录的时间
        SysUser sysUser1 = new SysUser();
        sysUser1.setId(sysUser.getId());
        sysUser1.setLoginTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(sysUser1);

        return  R.ok(map);
    }

    /**
     * 获取登陆人信息
     * @return
     */
    @GetMapping("/info")
    public R loadAdminInfo(){
        // 1. 从 Spring Security 上下文获取 Authentication 对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 2. 获取 Principal (这就是你在 UserDetailsService 里返回的 AdminLoginUser 对象)
        AdminLoginUser loginUser = (AdminLoginUser) authentication.getPrincipal();
        // 3. 直接拿出 SysUser
        SysUser sysUser = loginUser.getSysUser();
        // 4. 为了安全，要把密码置空再返回
        sysUser.setPassword(null);

        return R.ok(sysUser);
        /*String name = jwtTokenUtil.getUserNameFromToken(token);
        SysUser sysUser = sysUserService.getAdminInfoByUsername(name);
        return R.ok(sysUser);*/
    }

    /**
     * 更新管理员用户信息
     * @param id
     * @param adminInfoParam
     * @return
     */
    @PostMapping("/update/{id}")
    public R updateAdminInfo(@PathVariable("id") Long id, @RequestBody AdminInfoParam adminInfoParam){
        int result = sysUserService.updateAdminInfo(id,adminInfoParam);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 更新用户密码
     * @param param
     * @return
     */
    @PostMapping("/updatePassword")
    public R updatePassword(@RequestBody UpdatePasswordParam param){
        SysUser sysUser = sysUserMapper.selectByUsername(param.getUsername());
        if(!passwordEncoder.matches(param.getOldPassword(),sysUser.getPassword())){
            return R.error("老密码不正确,请重新输入");
        }
        SysUser sysUser1 = new SysUser();
        sysUser1.setId(sysUser.getId());
        sysUser1.setPassword(passwordEncoder.encode(param.getNewPassword()));
        int result = sysUserMapper.updateByPrimaryKeySelective(sysUser1);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 获取B端管理员列表
     * @param adminPageInfoQuery
     * @return
     */
    @GetMapping("/list")
    public R adminUserList(AdminPageInfoQuery adminPageInfoQuery){
        PageInfo<SysUser> sysUserPageInfo = sysUserService.selectAdminUserList(adminPageInfoQuery);
        return R.ok(sysUserPageInfo);
    }

    /**
     * B端超级管理员添加管理员用户
     * @param sysUser
     * @return
     */
    @PostMapping("/register")
    public R registerAdminUser(@RequestBody SysUser sysUser){
        Long id = sysUserService.addAdminUser(sysUser);
        return id >= 1 ? R.ok(id):R.error();
    }

    /**
     * 删除管理员用户
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public R deleteAdminUser(@PathVariable("id") Long id){
        int result = sysUserService.deleteAdmin(id);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 修改管理员可用状态(账户的启用状态)
     * @param id
     * @return
     */
    @PostMapping("/updateStatus/{id}")
    public R updateAdminStatus(@PathVariable("id") Long id,@RequestParam Integer status){
        int result = sysUserService.updateAdminStatus(id,status);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 获取后台菜单
     * @return
     */
    @GetMapping("/menu/treeList")
    public R menuTreeList(){
        List<SysMenu> sysMenuList = menuService.getMenuTreeList();
        return R.ok(sysMenuList);
    }



}
