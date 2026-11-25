import java.util.*;

public class MetroRoutePlanner {

    static class Edge {
        String destination;
        int time;

        Edge(String destination, int time) {
            this.destination = destination;
            this.time = time;
        }
    }

    static class Node {
        String station;
        int distance;

        Node(String station, int distance) {
            this.station = station;
            this.distance = distance;
        }
    }

    static Map<String, List<Edge>> graph = new LinkedHashMap<>();
    static Set<String> closedStations = new HashSet<>();

    // Create station
    static void addStation(String station) {
        graph.putIfAbsent(station, new ArrayList<>());
    }

    // Create connection
    static void addEdge(String a, String b, int time) {
        graph.get(a).add(new Edge(b, time));
        graph.get(b).add(new Edge(a, time));
    }

    // Metro network
    static void createNetwork() {
        addStation("Airport");
        addStation("Central");
        addStation("Market");
        addStation("Mall");
        addStation("Stadium");
        addStation("University");

        addEdge("Airport", "Central", 4);
        addEdge("Central", "Market", 4);
        addEdge("Market", "Mall", 3);
        addEdge("Mall", "Stadium", 5);
        addEdge("University", "Market", 6);
    }

    // Find station
    static String findStation(String name) {
        for (String s : graph.keySet()) {
            if (s.equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    // BFS - Minimum stops
    static List<String> bfs(String source, String destination) {

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(source);
        visited.add(source);
        parent.put(source, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(destination)) {
                break;
            }

            for (Edge edge : graph.get(current)) {
                String next = edge.destination;

                if (!closedStations.contains(next)
                        && !visited.contains(next)) {

                    visited.add(next);
                    parent.put(next, current);
                    queue.add(next);
                }
            }
        }

        return buildPath(parent, source, destination);
    }

    // Dijkstra - Minimum time
    static List<String> dijkstra(String source, String destination) {

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        PriorityQueue<Node> pq = new PriorityQueue<>(
            Comparator.comparingInt(n -> n.distance)
        );

        for (String station : graph.keySet()) {
            distance.put(station, Integer.MAX_VALUE);
        }

        distance.put(source, 0);
        parent.put(source, null);

        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {

            Node node = pq.poll();
            String current = node.station;

            if (node.distance != distance.get(current)) {
                continue;
            }

            if (current.equals(destination)) {
                break;
            }

            for (Edge edge : graph.get(current)) {

                String next = edge.destination;

                if (closedStations.contains(next)) {
                    continue;
                }

                int newTime = distance.get(current) + edge.time;

                if (newTime < distance.get(next)) {

                    distance.put(next, newTime);
                    parent.put(next, current);

                    pq.add(new Node(next, newTime));
                }
            }
        }

        return buildPath(parent, source, destination);
    }

    // Reconstruct route using Stack
    static List<String> buildPath(
            Map<String, String> parent,
            String source,
            String destination) {

        Stack<String> stack = new Stack<>();

        if (!parent.containsKey(destination)) {
            return new ArrayList<>();
        }

        String current = destination;

        while (current != null) {
            stack.push(current);
            current = parent.get(current);
        }

        List<String> path = new ArrayList<>();

        if (stack.isEmpty() || !stack.peek().equals(source)) {
            return path;
        }

        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }

        return path;
    }

    // Calculate travel time
    static int calculateTime(List<String> path) {

        int total = 0;

        for (int i = 0; i < path.size() - 1; i++) {

            String from = path.get(i);
            String to = path.get(i + 1);

            for (Edge edge : graph.get(from)) {
                if (edge.destination.equals(to)) {
                    total += edge.time;
                    break;
                }
            }
        }

        return total;
    }

    // Display route
    static void displayRoute(List<String> path) {

        if (path.isEmpty()) {
            System.out.println("No route available.");
            return;
        }

        int stops = path.size() - 1;
        int time = calculateTime(path);
        int fare = stops * 10;

        System.out.println("\nRoute: " +
                String.join(" -> ", path));

        System.out.println("Stops: " + stops);
        System.out.println("Travel time: " + time + " min");
        System.out.println("Fare: Rs. " + fare);
    }

    // Find route
    static void findRoute(Scanner sc) {

        System.out.print("Enter source station: ");
        String source = findStation(sc.nextLine().trim());

        System.out.print("Enter destination station: ");
        String destination = findStation(sc.nextLine().trim());

        if (source == null) {
            System.out.println("Source station not found.");
            return;
        }

        if (destination == null) {
            System.out.println("Station not found.");
            return;
        }

        if (source.equals(destination)) {
            System.out.println("You are already at " + source + ".");
            System.out.println("Stops: 0 | Travel time: 0 min | Fare: Rs. 0");
            return;
        }

        if (closedStations.contains(source)
                || closedStations.contains(destination)) {
            System.out.println("No route available. Station is closed.");
            return;
        }

        System.out.println("\n1. BFS (Minimum Stops)");
        System.out.println("2. Dijkstra (Minimum Time)");

        System.out.print("Choose algorithm: ");
        int choice = sc.nextInt();
        sc.nextLine();

        List<String> path;

        if (choice == 1) {
            path = bfs(source, destination);
        } else if (choice == 2) {
            path = dijkstra(source, destination);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        displayRoute(path);
    }

    // Close station
    static void closeStation(Scanner sc) {

        System.out.print("Enter station to close: ");
        String station = findStation(sc.nextLine().trim());

        if (station == null) {
            System.out.println("Station not found.");
        } else {
            closedStations.add(station);
            System.out.println(station + " closed successfully.");
        }
    }

    // Reopen station
    static void reopenStation(Scanner sc) {

        System.out.print("Enter station to reopen: ");
        String station = findStation(sc.nextLine().trim());

        if (station == null) {
            System.out.println("Station not found.");
        } else if (closedStations.remove(station)) {
            System.out.println(station + " reopened successfully.");
        } else {
            System.out.println(station + " is already open.");
        }
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        createNetwork();

        while (true) {

            System.out.println("\n===== METRO ROUTE PLANNER =====");
            System.out.println("1. Find Metro Route");
            System.out.println("2. Close Station");
            System.out.println("3. Reopen Station");
            System.out.println("4. Display Stations");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    findRoute(sc);
                    break;

                case 2:
                    closeStation(sc);
                    break;

                case 3:
                    reopenStation(sc);
                    break;

                case 4:
                    System.out.println("Metro Stations:");
                    for (String station : graph.keySet()) {
                        System.out.println(station +
                            (closedStations.contains(station)
                                ? " (Closed)" : " (Open)"));
                    }
                    break;

                case 5:
                    System.out.println("Thank you for using Metro Route Planner!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
