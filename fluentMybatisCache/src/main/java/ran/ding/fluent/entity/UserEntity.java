package ran.ding.fluent.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * UserEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@FluentMybatis(
    table = "user",
    schema = "test"
)
public class UserEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   */
  @TableField("guid")
  private String guid;

  /**
   */
  @TableField("name")
  private String name;

  /**
   */
  @TableField("phone")
  private String phone;

  /**
   */
  @TableField("city")
  private String city;

  /**
   */
  @TableField("region")
  private String region;

  /**
   */
  @TableField("cvv")
  private String cvv;

  @Override
  public final Class<? extends IEntity> entityClass() {
    return UserEntity.class;
  }

  @Override
  public final UserEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final UserEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
