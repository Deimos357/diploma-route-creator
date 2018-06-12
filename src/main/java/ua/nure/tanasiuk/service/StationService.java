package ua.nure.tanasiuk.service;

import org.springframework.stereotype.Service;
import ua.nure.tanasiuk.dao.StationDao;
import ua.nure.tanasiuk.dto.StationInRoute;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StationService {
    private final StationDao stationDao;
    private final double[][] distanceMatrix;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;

        distanceMatrix = stationDao.getDistanceMatrix();
    }

    public double[][] getDistanceMatrix(List<Integer> cities) {
        double[][] result = new double[cities.size()][];

        for (int i = 0; i < cities.size(); i++) {
            result[i] = new double[cities.size()];
            for (int j = 0; j < cities.size(); j++) {
                result[i][j] = distanceMatrix[cities.get(i) - 1][cities.get(j) - 1];
            }
        }

        return result;
    }

    public int getClosetStation(int curSt, List<StationInRoute> stations, List<Integer> visited) {
        final double[] min = {Double.MAX_VALUE};
        AtomicInteger result = new AtomicInteger(-1);

        stations.stream().map(StationInRoute::getStationId).forEach(st -> {
            if (distanceMatrix[curSt - 1][st - 1] < min[0] && !visited.contains(st)) {
                min[0] = distanceMatrix[curSt - 1][st - 1];
                result.set(st);
            }
        });

        return result.get();
    }
}
