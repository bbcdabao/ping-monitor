package bbcdabao.pingmonitor.manager.test;

import bbcdabao.pingmonitor.common.extraction.annotation.ExtractionFieldMark;
import lombok.Data;

@Data
public class MyPlug {
    @ExtractionFieldMark(descriptionCn = "我的ID", descriptionEn = "my id")
    private long myid;
    @ExtractionFieldMark(descriptionCn = "信息", descriptionEn = "info ex")
    private String info;
}
