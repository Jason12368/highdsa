package pers.husen.highdsa.web.shiro.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import pers.husen.highdsa.common.constant.RedisCacheKeyPrefix;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Desc 密码验证服务,带失败重试次数限制
 *
 * @Author 何明胜
 *
 * @Created at 2018年3月29日 下午11:30:16
 * 
 * @Version 1.0.1
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

	private Cache<String, AtomicInteger> passwordRetryCache;

	public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
		passwordRetryCache = cacheManager.getCache("passwordRetryCache");
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		String username = (String) token.getPrincipal();
		// retry count + 1
		AtomicInteger retryCount = passwordRetryCache.get(RedisCacheKeyPrefix.SHIRO_LOGIN_FAIL_COUNT + username);
		System.out.println("retryCount: " + retryCount);

		if (retryCount == null) {
			System.out.println("retryCount 为Null, 初始化为0");
			retryCount = new AtomicInteger(0);
			passwordRetryCache.put(username, retryCount);
		}
		if (retryCount.incrementAndGet() > 5) {
			System.out.println("retryCount 大于5");
			// if retry count > 5 throw
			throw new ExcessiveAttemptsException();
		}

		boolean matches = super.doCredentialsMatch(token, info);
		if (matches) {
			// clear retry count
			passwordRetryCache.remove(username);
		}
		return matches;
	}
}