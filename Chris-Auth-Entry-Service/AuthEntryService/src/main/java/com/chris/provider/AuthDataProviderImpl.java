/**
 * MIT License
 * <p>
 * Copyright (c) 2024 Chris Yang
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.chris.provider;

import com.chris.dao.AuthAccessDao;
import com.chris.entity.AuthUser;
import com.chris.exception.AuthServiceException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;
import static com.chris.util.AuthAccessConstants.AUTH_DATA_PROVIDER_BEAN;
import static com.chris.util.AuthAccessConstants.BCRYPT_ENCODER_BEAN;

/**
 * called by authentication manager to load the auth data for validation
 */
@Component(value = AUTH_DATA_PROVIDER_BEAN)
public class AuthDataProviderImpl implements AuthDataProvider {
    private Logger _LOG = LoggerFactory.getLogger(AuthDataProviderImpl.class);

    private final AuthAccessDao _accessDao;
    private final PasswordEncoder _encoder;

    @Value("${app.auth.encoder.enabled:true}")
    private boolean _encoderEnabled;

    @Autowired
    public AuthDataProviderImpl(
            @Qualifier(value = AUTH_ACCESS_DAO_BEAN) AuthAccessDao accessDao,
            @Qualifier(value = BCRYPT_ENCODER_BEAN) PasswordEncoder encoder) {
        _accessDao = accessDao;
        _encoder = encoder;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is constructed for authentication manager...", AUTH_DATA_PROVIDER_BEAN);
        _LOG.warn("password encoder enabled: {}", _encoderEnabled);
    }

    /**
     * validate the "email" and password with the database, and load the role with authentication
     *
     * @param authentication the authentication request object.
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            //username is the 'email' from the client
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            AuthUser user = _accessDao.findUserByEmail(username);
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (user != null) {
                if (_encoderEnabled) {
                    //with encoded password
                    if (_encoder.matches(password, user.getPassword())) {
                        _genAuthorities(user, authorities);
                    } else {
                        throw new BadCredentialsException("invalid password...");
                    }
                } else {
                    //with plain password
                    if (password.equals(user.getPassword())) {
                        _genAuthorities(user, authorities);
                    } else {
                        throw new BadCredentialsException("invalid password...");
                    }
                }
            } else {
                throw new BadCredentialsException("invalid username...");
            }
            return new UsernamePasswordAuthenticationToken(username, user.getPassword(), authorities);
        } catch (Exception exp) {
            throw new AuthServiceException("fails to authenticate the user: " + exp);
        }
    }

    private static void _genAuthorities(AuthUser user, List<GrantedAuthority> authorities) {
        user.getRoles().stream().forEach(x -> authorities.add(new SimpleGrantedAuthority(x.getName())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
