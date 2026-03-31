# 开发规范（项目上下文）

每次完成任务前后，必须逐条检查任务完成方案和执行是否符合以下开发规范：

1. 本mod有i18n系统，通过`Translator.tr()`函数实现，所有语言都要同步实现。原则上不要使用`Text.literal()`。需实现`resource`里面对应的英文项目。对于发送给玩家的文本，需用MOTD格式制作比较美观的色彩和加粗下划线等效果，但不要引入Unicode特殊符号。
   - 不要使用单引号包围参数，否则参数无法显示。
   - **凡是语言文件条目值中含有单引号 `'`（如英文中的 `it's`、`don't` 等），且该条目以带参数的方式调用（即传入 `{0}` 等占位符），必须将单引号转义为 `''`（两个单引号）。这是因为 `java.text.MessageFormat` 将 `'` 视为转义字符，未转义的单引号会导致占位符或后续内容被错误解析。无参数调用的条目不受此影响。**
2. 配置类存储所有具体数值。任何具体数值都应写入对应的配置类，不要在业务代码中硬编码数值。
3. 数据库维护类负责所有持久化操作。凡涉及改动数据成员变量的操作，必须检查是否涉及数据库存储的改动。
4. 任何Command都在`CommandRegister`中的`register()`函数里面注册，并在同一文件中提取参数，并调用application对应实现。没有找到合适的调用的时候，要自己实现模块。
5. 命令参数中涉及 Region 名称或相关名称的所有 SuggestionProvider，必须对不满足"全部字符均为 ASCII 字母或数字"条件的名称用双引号包裹后再 suggest，即使用 `if (!name.all { it.isLetterOrDigit() && it.code < 128 }) builder.suggest("\"$name\"") else builder.suggest(name)` 的形式。
6. 原则上不要新建新的class，也不要添加Comments.
7. 修改机制之后，必须检查`README.md`进行修改。以玩家侧的游戏机制介绍为主。每次完成任务前，须确认`README.md`的changelog部分已同步记录本次更改，但不要随意新建版本或更新版本号。
8. 不使用git，除非prompt要求。进行prompt提交时，应符合git log里面先前的一般commit格式，简洁规范，不要添加Co-authored-by等trailer。
9. 本项目依赖`ImyvmWorldGeo`从Maven仓库拉取制品，相邻目录下的本地源码**不保证**与实际构建所使用的制品版本一致。实现调用`ImyvmWorldGeo` API的功能时，请对照**已发布制品**验证方法签名，不要将本地源文件视为规范的API参考。
10. 测试要包含./gradlew runServer.
11. 未说明清楚的机制、语言文件用名和感到机制模糊的地方等等应该向操作者提问。不要为了确认需求终止对话。
12. 本项目跟IMYVMWorldGeo Core和CommunityAddon要高度协作，互相参考。
