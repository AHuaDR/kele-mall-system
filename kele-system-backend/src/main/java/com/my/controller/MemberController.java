package com.my.controller;

import com.my.dto.MemberLoginParam;
import com.my.mapper.UmsMemberMapper;
import com.my.model.UmsMember;
import com.my.result.R;
import com.my.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 15:07
 * @description
 */
@RestController
@RequestMapping("/api/sso")
public class MemberController {
    @Resource
    private UmsMemberMapper umsMemberMapper;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginParam param) throws Exception {
        UmsMember umsMember = umsMemberMapper.selectByPhone(param.getPhone());
        if(umsMember==null){
            return R.error("账号不存在");
        }
        if(!passwordEncoder.matches(param.getPassword(),umsMember.getPassword())){
            return R.error("密码错误");
        }
        if (!umsMember.getStatus()){
            return R.error("账号已封禁");
        }
        if (!passwordEncoder.matches(param.getPassword(), umsMember.getPassword())) {
            return R.error("密码错误");
        }

        // 5. 生成 Token
        String token = jwtTokenUtil.generateToken(umsMember.getPhone());

        return R.ok(token);
    }
}
