package org.yong.util.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.yong.util.file.xml.annotation.FieldType;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;

@Data
@NoArgsConstructor
@XmlTag("TourTask")
public class TourTask {

    @XmlField(name = "taskName", type = FieldType.ATTRIBUTE)
    private String taskName;

    @XmlField
    private int tourType;

    @XmlField
    private String robotID;
}
