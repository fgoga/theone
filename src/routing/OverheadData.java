package routing;

import com.opencsv.bean.CsvBindByPosition;

public class OverheadData {

        @CsvBindByPosition(position = 0)
        private double time;

        @CsvBindByPosition(position = 1)
        private Long overheadValue;

        public OverheadData() {}

        public OverheadData(double time, Long overheadValue) {
            this.time = time;
            this.overheadValue = overheadValue;
        }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Long getOverheadValue() {
        return overheadValue;
    }

    public void setOverheadValue(Long overheadValue) {
        this.overheadValue = overheadValue;
    }
}
