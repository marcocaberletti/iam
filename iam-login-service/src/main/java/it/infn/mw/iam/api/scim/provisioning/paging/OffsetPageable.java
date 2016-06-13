package it.infn.mw.iam.api.scim.provisioning.paging;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.base.Preconditions;

public class OffsetPageable implements Pageable {

  private final int offset;
  private final int count;

  public OffsetPageable(int offset, int count) {
    Preconditions.checkArgument(offset >= 0,
      "offset must be greater or equal to 0");

    Preconditions.checkArgument(count >= 1, "count must be a positive integer");

    this.offset = offset;
    this.count = count;
  }

  @Override
  public int getPageNumber() {

    return offset / count;
  }

  @Override
  public int getPageSize() {

    return count;
  }

  @Override
  public int getOffset() {

    return offset;
  }

  @Override
  public Sort getSort() {

    return null;
  }

  @Override
  public Pageable next() {

    return new OffsetPageable(offset + count, count);
  }

  @Override
  public Pageable previousOrFirst() {

    int newOffset = offset - count;
    if (newOffset < 0) {
      newOffset = 0;
    }

    return new OffsetPageable(newOffset, count);

  }

  @Override
  public Pageable first() {

    return new OffsetPageable(0, count);
  }

  @Override
  public boolean hasPrevious() {

    return offset > 0;
  }

  @Override
  public String toString() {

    return "OffsetPageable [offset=" + offset + ", count=" + count + "]";
  }

}