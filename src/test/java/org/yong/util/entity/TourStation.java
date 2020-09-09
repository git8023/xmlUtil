package org.yong.util.entity;

import lombok.Data;
import org.yong.util.file.xml.annotation.FieldType;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;

import java.util.List;

/**
 * 巡检站
 */
@Data
@XmlTag
public class TourStation {
    @XmlField
    private String stationName;

    @XmlField
    private int location;

    @XmlField
    private int endLocation;

    @XmlField
    private int speed;

    @XmlField(type = FieldType.TAG)
    private List<TourAction> tourAction;
}
