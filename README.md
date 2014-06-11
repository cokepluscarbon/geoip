geoip
=====

geoip是基于ip.taobao.com的IP库，所有的数据均来自于ip.taobao.com。由于ip.taobao.com限制10dps，所以在大并发下ip.taobao.com并不适用。geoip采用分布式计算采集ip.taobao.com的IP库信息并压缩成2M左右大小的二级制文件，提供高效、准确的IP信息查询，以此同时，对比其他的IP库，geoip提供更多的地理相关信息。

## 重构方向：

* server和client的代码更加简洁、清晰、高效
* 统一编码
* 双向验证
* 并发安全
* 智能调整请求线程
* 保存client处理记录
* ip解析超时处理
* Linux和Windows脚本
* IP库文件压缩
* IP库检索算法
* IP库数据结构
* IP库准确率校验
* IP库漏查检查与确认
* 各个国家IP数据库列表
* tcp-keepalive超时问题
* server和client日志

## 数据库格式

起始IP,结束IP,个数,城市,城市\_EN,地区/省份,地区/省份\_EN,国家,国家\_EN,国家\_CODE,ISP

221.4.213.0,221.4.213.255,255,珠海,Zhuhai,广东省,Guangdong,中国,China,CN,联通
