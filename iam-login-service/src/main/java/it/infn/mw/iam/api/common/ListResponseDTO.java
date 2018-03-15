package it.infn.mw.iam.api.common;

import java.util.List;
import org.springframework.data.domain.Page;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListResponseDTO<T> {

  private final long totalResults;
  private final int itemsPerPage;
  private final int startIndex;

  private List<T> resources;

  @JsonCreator
  public ListResponseDTO(@JsonProperty("Resources") List<T> resources,
      @JsonProperty("totalResults") long totalResults, @JsonProperty("startIndex") int startIndex,
      @JsonProperty("itemsPerPage") int itemsPerPage) {

    this.resources = resources;
    this.totalResults = totalResults;
    this.itemsPerPage = itemsPerPage;
    this.startIndex = startIndex;
  }

  private ListResponseDTO(Builder<T> builder) {
    this.totalResults = builder.totalResults;
    this.startIndex = builder.startIndex;
    this.itemsPerPage = builder.itemsPerPage;
    this.resources = builder.resources;
  }

  public long getTotalResults() {
    return totalResults;
  }

  public int getItemsPerPage() {
    return itemsPerPage;
  }

  public int getStartIndex() {
    return startIndex;
  }

  @JsonProperty("Resources")
  public List<T> getResources() {
    return resources;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }


  public static class Builder<T> {
    private long totalResults;
    private int itemsPerPage;
    private int startIndex;
    private List<T> resources;

    public <S> Builder<T> fromPage(Page<S> page, OffsetPageable op) {
      this.totalResults = page.getTotalElements();
      this.itemsPerPage = page.getNumberOfElements();
      this.startIndex = op.getOffset() + 1;
      return this;
    }

    public Builder<T> totalResults(long totalResults) {
      this.totalResults = totalResults;
      return this;
    }

    public Builder<T> itemsPerPage(int itemsPerPage) {
      this.itemsPerPage = itemsPerPage;
      return this;
    }

    public Builder<T> startIndex(int startIndex) {
      this.startIndex = startIndex;
      return this;
    }

    public Builder<T> resources(List<T> resources) {
      this.resources = resources;
      return this;
    }

    public ListResponseDTO<T> build() {
      return new ListResponseDTO<>(this);
    }
  }
}