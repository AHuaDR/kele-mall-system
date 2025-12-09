package com.my.security;

import com.my.model.SysRole;
import com.my.model.SysUser;
import io.micrometer.common.lang.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 13:31
 * @description
 */
@Data
@NoArgsConstructor
public class AdminLoginUser implements UserDetails {

    private SysUser sysUser;
    private List<SysRole> roleList;

    private List<SimpleGrantedAuthority> authorities;

    public AdminLoginUser(SysUser sysUser, List<SysRole> roleList) {
        this.sysUser = sysUser;
        this.roleList = roleList;

        this.authorities = new ArrayList<>();
        if (roleList != null) {
            for (SysRole role : roleList) {
                this.authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return sysUser.getStatus() == 1;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roleList.stream()
                .filter(role -> role.getStatus() == 1)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPassword() {
        return sysUser.getPassword();
    }

    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

}
