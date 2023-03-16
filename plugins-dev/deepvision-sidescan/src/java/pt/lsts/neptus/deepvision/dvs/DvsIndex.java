package pt.lsts.neptus.deepvision.dvs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DvsIndex implements Serializable {
    private static final long serialVersionUID = 1L;

    long firstTimestampHigh = -1;
    long lastTimestampHigh = -1;

    long firstTimestampLow = -1;
    long lastTimestampLow = -1;

    long numberOfPackets = -1;

    boolean hasHigh = false;
    boolean hasLow = true;

    LinkedHashMap<Long, ArrayList<Long>> positionMapHigh = new LinkedHashMap<Long, ArrayList<Long>>();
    LinkedHashMap<Long, ArrayList<Long>> positionMapLow = new LinkedHashMap<Long, ArrayList<Long>>();

    ArrayList<Float> frequenciesList = new ArrayList<Float>();
    ArrayList<Integer> subSystemsList = new ArrayList<Integer>();

}
