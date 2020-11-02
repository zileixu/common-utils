## 配置中心zk目录结构
### 共享配置存储数据节点,持久化数据节点
/AI_CONFCENTER/${serviceType}/share
### 节点本身的配置（包含共享的），临时数据节点
/AI_CONFCENTER/${serviceType}/self/${ip}_${port}
### 共享配置信息key字段必须包含“.share.”字符串，如下
confcenter.global.share.enableExLog=true
