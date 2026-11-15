import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class MetroRoutePlanner {

    static Map<String, List<String>> metro = new HashMap<>();
 
    static void addRoute(String a, String b) {
        metro.putIfAbsent(a, new ArrayList<>());
        metro.putIfAbsent(b, new ArrayList<>());

        metro.get(a).add(b);
        metro.get(b).add(a);
    }

    static void findRoute(String start, String end) {
        Queue<String> q = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();

        q.add(start);
        parent.put(start, null);

        while (!q.isEmpty()) {
            String current = q.poll();

            if (current.equals(end))
                break;

            for (String next : metro.getOrDefault(
                    current, new ArrayList<>())) {

                if (!parent.containsKey(next)) {
                    parent.put(next, current);
                    q.add(next);
                }
            }
        }

        if (!parent.containsKey(end)) {
            System.out.println("No route found!");
            return;
        }

        List<String> path = new ArrayList<>();
        String station = end;

        while (station != null) {
            path.add(station);
            station = parent.get(station);
        }

        Collections.reverse(path);

        System.out.println("Shortest Route: " + path);
        System.out.println("Total Stations: " + path.size());
        System.out.println("Stops: " + (path.size() - 1));
    }

    public static void main(String[] args) {

        addRoute("A", "B");
        addRoute("B", "C");
        addRoute("C", "D");
        addRoute("A", "E");
        addRoute("E", "D");
        addRoute("D", "F");

        Scanner sc = new Scanner(System.in);

        System.out.println("Metro Stations: A, B, C, D, E, F");

        System.out.print("Enter source station: ");
        String start = sc.next().toUpperCase();

        System.out.print("Enter destination station: ");
        String end = sc.next().toUpperCase();

        findRoute(start, end);

        sc.close();
    }
}

//-------OUTPUT----------


// Metro Stations: A, B, C, D, E, F
// Enter source station: A
// Enter destination station: D
// Shortest Route: [A, E, D]
// Total Stations: 3
// Stops: 2