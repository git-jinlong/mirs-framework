package com.github.mirs.banxiaoxiao.framework.web.page;

/**
 * A bean class holds some values used for paging.
 *
 * @author zw
 */
public class PageParameter {

  private final int pageNumber;
  private final int pageSize;
  private final String orderBy;

  private final long offset;

  public enum Order {asc, desc}

  private final Order order;

  public PageParameter(int pageNumber, int pageSize, String orderBy, Order order) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.orderBy = orderBy;
    this.order = order;
    this.offset = (long) ((pageNumber - 1) * pageSize);
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public int getPageSize() {
    return pageSize;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public long getOffset() {
    return offset;
  }

  public Order getOrder() {
    return order;
  }
}
