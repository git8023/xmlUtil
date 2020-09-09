package org.yong.util.entity;

import lombok.Data;
import org.yong.util.file.xml.annotation.FieldType;
import org.yong.util.file.xml.annotation.PathRelation;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;

/**
 * 巡检动作
 */
@Data
@XmlTag
public class TourAction {
    @XmlField
    private int mountDvsType;

    @XmlField
    private String actionName;

    @XmlField
    private int detectedDvsType;

    @XmlField(name = "time", type = FieldType.ATTRIBUTE, hierarchy = PathRelation.CHILDREN, path = "SensorInfo")
    private long sensorInfoTime;

    @XmlField(type = FieldType.TAG)
    private SnapshotPosition snapshotPosition;
}
