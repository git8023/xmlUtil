package org.yong.util.entity;

import lombok.Data;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;

@Data
@XmlTag
public class TourLine {

    @XmlField
    private String lineName;

}
