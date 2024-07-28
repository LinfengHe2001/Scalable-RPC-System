package edu.duke.lh426.registry;


import edu.duke.lh426.model.ServiceMetaInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务本地缓存
 */
//public class RegistryServiceCache {
//
//    /**
//     * 服务缓存
//     */
//    List<ServiceMetaInfo> serviceCache;
//
//    /**
//     * 写缓存
//     *
//     * @param newServiceCache
//     * @return
//     */
//    void writeCache(List<ServiceMetaInfo> newServiceCache) {
//        this.serviceCache = newServiceCache;
//    }
//
//    /**
//     * 读缓存
//     *
//     * @return
//     */
//    List<ServiceMetaInfo> readCache() {
//        return this.serviceCache;
//    }
//
//    /**
//     * 清空缓存
//     */
//    void clearCache() {
//        this.serviceCache = null;
//    }
//}

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     *
     * @param newServiceCache
     * @return
     */
    public void  writeCache(String key, List<ServiceMetaInfo> newServiceCache){
        serviceCache.put(key, newServiceCache);
    }

    /**
     * 读缓存
     *
     * @return
     */
    public List<ServiceMetaInfo> readCache(String key){
        return serviceCache.get(key);
    }

    /**
     * 清空缓存
     */
    public void clearCache(String key){
        serviceCache.remove(key);
    }
}

