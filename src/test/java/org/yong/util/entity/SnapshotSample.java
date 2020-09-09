package org.yong.util.entity;

import lombok.Data;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;

/**
 * 快照样本
 */
@Data
@XmlTag
public class SnapshotSample {

    @XmlField
    private String image;
    @XmlField
    private String analysis;
    @XmlField
    private String time;

}
