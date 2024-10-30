# ping-monitor

# 拨测系统思路文字描述：

- 拨测机器人：采用SPI技术实现插件，每个拨测插件的唯一名字用java包名代替  
例如，下面是3个拨测插件唯一名字:  
```
plugs {
	com.xxx.sss.PingCallTest,
	com.xxx.sss.HttpCallTest,
	com.xxx.sss.XXXXCallTest
}
```
当拨测机器人启动后，会先注册拨测插件模版，然后注册自己  




zookeeper数据结构，以下是在一个命名空间下
```
拨测机器人路径 {
	param-templates路径 {
		com.xxx.sss.CtestParam = vaule 是参数模版 不同版本包名必然不同比如CtestParam1
		com.xxx.sss.Cssk = vaule 是参数模版 不同版本包名必然不同比如CtestParam1
		com.xxx.sss.Cmm = vaule 是参数模版 不同版本包名必然不同比如CtestParam1
	}
	机器人注册路径 子节点都是临时节点 {
		临时节点 实例1 {
			plugs {
				com.xxx.sss.CtestParam,
				com.xxx.sss.Cssk,
				com.xxx.sss.Cmm
			}
			拨测任务 列表{
				task-01 {
					拨测周期
					拨测结果
				}
				task-02 {
					拨测周期
					拨测结果
				}
			}
		}
		临时节点 实例2 {
			plugs {
				com.xxx.sss.Cssk,
				com.xxx.sss.Cmm
			}
			拨测任务 列表{
				task-01 {
					拨测周期
					拨测结果
				}
			}
		}
		临时节点 实例3 {
			plugs {
				com.xxx.sss.CtestParam,
				com.xxx.sss.Cssk
			}
			拨测任务 列表{
				task-02 {
					拨测周期
					拨测结果
				}
			}
		}
	}
}

拨测任务路径 {
	任务名称：task-01 {
		com.xxx.sss.CtestParam {
			拨测配置
		}
	}
	任务名称：task-02 {
		com.xxx.sss.Cssk {
			拨测配置
		}
	}
}

```
重点
(1) 拨测 的 参数  例如 com.xxx.sss.CtestParam  包名，绑定插件 和 一些列动作
(2) com.xxx.sss.CtestParam 对应每个字段，作为模版存储 key  和  描述参数类型，开发一个注解支持注解CtestParam



二次开发需要：
开发拨测机器人 的 能力插件

一个插件ID 对应---
第一 对应拨测机器人里面的插件代码，可以用SPI技术，或者springboot方案插件
第二 对应 拨测任务 里面的任务配置，param 里面的结构体

一个插件完成，可以添加配置拨测任务，并且该拨测任务 只能由 具有该拨测插件能力的机器人完成

以上就是一播放组，对应zookeeper里面一个命名空间

import com.fasterxml.jackson.databind.ObjectMapper;


public class TemplateField {
    private String key;       // 属性的 key
    private String type;      // 属性的类型（如 String, Integer 等）

    // Constructor, Getters and Setters
    public TemplateField(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


public class Template {
    private List<TemplateField> fields;

    // Constructor, Getters and Setters
    public Template(List<TemplateField> fields) {
        this.fields = fields;
    }

    public List<TemplateField> getFields() {
        return fields;
    }

    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }
}


my-spi-project
├── api
│   └── pom.xml         // SPI 接口的定义
├── implementation1
│   ├── src
│   └── pom.xml         // 第一个实现
├── implementation2
│   ├── src
│   └── pom.xml         // 第二个实现
└── main
    ├── src
    └── pom.xml         // 主应用程序


https://chatgpt.com/share/671b12d6-1298-8003-b7ed-919f9b28b110
