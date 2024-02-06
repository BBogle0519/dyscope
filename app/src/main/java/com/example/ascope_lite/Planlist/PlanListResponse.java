package com.example.ascope_lite.Planlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanListResponse {
    @SerializedName("count")
    public String count;

    @SerializedName("next")
    public String next;

    @SerializedName("previous")
    public String previous;

    @SerializedName("results")
    public List<PlanListDTO> results;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<PlanListDTO> getResults() {
        return results;
    }

    public void setResults(List<PlanListDTO> results) {
        this.results = results;
    }
}
