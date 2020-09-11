package org.yong.util.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yong.util.file.xml.annotation.FieldType;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;


@Data


// @XmlTag 建立TourAction和XMLObject映射关系
@XmlTag
@NoArgsConstructor
@AllArgsConstructor
public class TourAction {

    @XmlField
    // @XmlField(name = "mountDvsType", type = FieldType.ATTRIBUTE)
    // 以上两种注解方式完全一致
    private int mountDvsType;

    @XmlField
    private String actionName;

    @XmlField
    private int detectedDvsType;

    /*
     * <TourAction mountDvsType="1" actionName="默认动作名称" detectedDvsType="0">
     *     <SensorInfo time="1599058642"/>
     * </TourAction>
     * 通过 path&name 读取子孙节点属性(或标签体)的值
     * path + name => TourAction.SensorInfo[time]
     */
    @XmlField(name = "time", type = FieldType.ATTRIBUTE, path = "SensorInfo")
    private long sensorInfoTime;

    /*
     * <SnapshotPosition arm1="0" arm2="90" arm3="0" arm4="0" arm5="0" arm6="0" zoom="1" thermal="1">
     *     <SnapshotSample image="仪表_20200902_145725.jpg" analysis="" time="1599058645"/>
     * </SnapshotPosition>
     * 指明 snapshotPosition 数据来源是直接子标签, 具体映射方案由 SnapshotPosition 类的 @XmlTag & @XmlField 决定
     */
    @XmlField(type = FieldType.TAG)
    private SnapshotPosition snapshotPosition;
}
