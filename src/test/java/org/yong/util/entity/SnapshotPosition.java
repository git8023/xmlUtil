package org.yong.util.entity;

import lombok.Data;
import org.yong.util.file.xml.annotation.FieldType;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;

/**
 * 快照位置
 */
@Data
@XmlTag
public class SnapshotPosition {

    @XmlField
    private double arm1;

    @XmlField
    private double arm2;

    @XmlField
    private double arm3;

    @XmlField
    private double arm4;

    @XmlField
    private double arm5;

    @XmlField
    private double arm6;

    @XmlField
    private double zoom;

    @XmlField
    private double thermal;

    @XmlField(type = FieldType.TAG)
    private SnapshotSample snapshotSample;

}
