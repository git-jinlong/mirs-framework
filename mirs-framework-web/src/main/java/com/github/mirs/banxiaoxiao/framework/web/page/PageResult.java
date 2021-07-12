package com.github.mirs.banxiaoxiao.framework.web.page;

import java.io.Serializable;
import java.util.List;

/**
 * A class holds the result for paging.
 *
 * @author zw
 */
public class PageResult<T> implements Serializable{

  private static final long serialVersionUID = -5661021180920021366L;
  /**
   * The value of row count.
   */
  private  Long total;

  /**
   * Currently page result.
   */
  private  List<T> rows;

  public PageResult(){

  }

  public PageResult(Long total, List<T> rows) {
    this.total = total;
    this.rows = rows;
  }

  public Long getTotal() {
    return total;
  }

  public List<T> getRows() {
    return rows;
  }
}
