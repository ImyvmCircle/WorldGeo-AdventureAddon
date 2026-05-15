---
applyTo: "src/main/kotlin/**/config/**,src/main/kotlin/**/database/**,src/main/kotlin/**/domain/**"
---

# 配置与持久化规则

1. 具体数值放入配置类，不在业务代码中硬编码具体数值。
2. 数据成员变化同步检查数据库或持久化逻辑。
3. 新增持久化字段必须提供旧数据可用的默认值。
4. 新字段只能追加在当前数据块末尾，不插入已有字段之间。
