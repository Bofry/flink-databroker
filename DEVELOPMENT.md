**August 2021**  |  **v1.0**

DataBroker 開發指南
==================================

###### tags: `flink-databroker` `tutorial`

----------------------------------------------------------------
#### 目錄
  - [編譯Jar](#編譯jar)
----------------------------------------------------------------

## 編譯Jar
 
1. clone 專案至本機上。
```bash
$ cd ${workspace}
$ git clone https://github.com/Bofry/flink-databroker
```
2. cd 至專案目錄。
```bash
$ cd ${workspace}/databroker
```
3. 使用 maven 編譯。
```bash
$ mvn clean package -Dmaven.test.skip=true
```
> 注：每次編譯前，請先`git pull`取得最新的程式碼，再進行編譯。

4. jar項目與說明
- 於`core`專案的`target`目錄下的`core-1.0.0-SNAPSHOT-jar-with-dependencies.jar`
  - 用於放置至`jobmanager`、`taskmanager`下的`flink/lib`目錄下。
- 於`application`專案的`target`目錄下的`application-1.0.0-SNAPSHOT.jar`
  - 用於上傳至`jobmanager`後台做`job`啟動。

[🔝回目錄](#%e7%9b%ae%e9%8c%84)