package problem2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

public class Problem2 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {  }

        System.out.println("Before goint to the Panel there are some things you should follow:-");
        System.out.println(" * You can add a node to a place by clicking at it");
        System.out.println(" * You can add an edge by dragging it form node to node OR by using Add an Edge button");
        System.out.println(" * You can add weight to an edge by clicking on it(Default=1)");
        System.out.println(" * You can Reset, Run or Exit though buttons on the panel");
        System.out.println("Press any key to continue");
        br.readLine();
        JFrame j = new JFrame();
        j.setTitle("Prim's Algorithm");

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setSize(new Dimension(900, 600));
        j.add(new MainWindow());
        j.setVisible(true);
    }
}

class PrimAlgorithm {
    private boolean safe = false;
    private String message = null;

    private Graph graph;
    private Map<Node, Node> predecessors;
    private Map<Node, Integer> distances;

    private PriorityQueue<Node> unvisited;
    private HashSet<Node> visited;
    public int parent[]=new int[100];

    public ArrayList<ArrayList<Integer>> aMatrix=new ArrayList<ArrayList<Integer>>();
    
    public PrimAlgorithm(Graph graph) {
       
        this.graph = graph;
        predecessors = new HashMap<>();
        distances = new HashMap<>();

        for(Node node : graph.getNodes()){
            distances.put(node, Integer.MAX_VALUE);
        }
        visited = new HashSet<>();

        safe = evaluate();
    }

    public class NodeComparator implements Comparator<Node>  {
        @Override
        public int compare(Node node1, Node node2) {
            return distances.get(node1) - distances.get(node2);
        }
    };

   
    private boolean evaluate(){
        if(graph.getSource()==null){
            message = "Source must be present in the graph";
            return false;
        }
        for(Node node : graph.getNodes()){
            if(!graph.isNodeReachable(node)){
                message = "Graph contains unreachable nodes";
                return false;
            }
        }
        return true;
    }

    public void run() throws IllegalStateException {
        if(!safe) {
            throw new IllegalStateException(message);
        }        
        for(Node node : graph.getNodes()){
            ArrayList<Integer> row=new ArrayList<Integer>();
            for(int j=0;j<graph.getNodes().size();j++){
                row.add(0);
            }
            aMatrix.add(row);
        }
        for(Edge edge : graph.getEdges()) {
            Node a=edge.getNodeOne();
            Node b=edge.getNodeTwo();
            aMatrix.get(a.getId()-1).set(b.getId()-1,edge.getWeight());
            aMatrix.get(b.getId()-1).set(a.getId()-1,edge.getWeight());
        }
        primMST();
        for(Node node : graph.getNodes()) {
            node.setPath(getPath1());
        }
        graph.setSolved(true);
        
    }
    
    
        void primMST() 
        { 
            int V = aMatrix.size();
            int key[] = new int [V];
            Boolean mstSet[] = new Boolean[V]; 
            for (int i = 0; i < V; i++) 
            { 
                key[i] = Integer.MAX_VALUE; 
                mstSet[i] = false; 
            }  
            key[0] = 0;   
            parent[0] = -1; 
            for (int count = 0; count < V-1; count++){
                int u = minKey(key, mstSet);  
                mstSet[u] = true;  
                for (int v = 0; v < V; v++)
                    if (aMatrix.get(u).get(v)!=0 && mstSet[v] == false && 
                        aMatrix.get(u).get(v) < key[v]) 
                    { 
                        parent[v] = u; 
                        key[v] = aMatrix.get(u).get(v); 
                    } 
            }
        }
        
        int minKey(int key[], Boolean mstSet[]) 
        { 
            int min = Integer.MAX_VALUE, min_index=-1; 

            for (int v = 0; v < aMatrix.size(); v++) 
                if (mstSet[v] == false && key[v] < min) 
                { 
                    min = key[v]; 
                    min_index = v; 
                } 

            return min_index; 
        } 
        
        public java.util.List<Node> getPath1() 
        { 
            java.util.List<Node> path = new ArrayList<>();
            for (int i = 1; i < aMatrix.size(); i++) {
                for(Node node : graph.getNodes()) {
                    if(parent[i]+1==node.getId()){
                        path.add(node);
                    }
                }
                for(Node node : graph.getNodes()) {
                    if(i+1==node.getId()){
                        path.add(node);
                    }
                }
            }

            Collections.reverse(path);

            return path;
        }

    public java.util.List<Node> getLastNode()
    {
       return graph.getNodes();
    }
    public Integer getDestinationDistance(){
        return distances.get(graph.getDestination());
    }

    public Integer getDistance(Node node){
        return distances.get(node);
    }

    public java.util.List<Node> getDestinationPath() {
        return getPath1();
    }
}

class DrawUtils {
    private Graphics2D g;
    private static int radius = 20;

    public DrawUtils(Graphics2D graphics2D){
        g = graphics2D;
    }

    public static boolean isWithinBounds(MouseEvent e, Point p) {
        int x = e.getX();
        int y = e.getY();

        int boundX = (int) p.getX();
        int boundY = (int) p.getY();

        return (x <= boundX + radius && x >= boundX - radius) && (y <= boundY + radius && y >= boundY - radius);
    }

    public static boolean isOverlapping(MouseEvent e, Point p) {
        int x = e.getX();
        int y = e.getY();

        int boundX = (int) p.getX();
        int boundY = (int) p.getY();

        return (x <= boundX + 2.5*radius && x >= boundX - 2.5*radius) && (y <= boundY + 2.5*radius && y >= boundY - 2.5*radius);
    }

    public static boolean isOnEdge(MouseEvent e, Edge edge) {

        int dist = distToSegment( e.getPoint(), edge.getNodeOne().getCoord(),  edge.getNodeTwo().getCoord() );
        if (dist<6)
            return true;
        return false;
    }

    public static Color parseColor(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    public void drawWeight(Edge edge) {
        Point from = edge.getNodeOne().getCoord();
        Point to = edge.getNodeTwo().getCoord();
        int x = (from.x + to.x)/2;
        int y = (from.y + to.y)/2;

        int rad = radius/2;
        g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
        drawWeightText(String.valueOf(edge.getWeight()), x, y);
    }

    public void drawPath(java.util.List<Node> path) {
        java.util.List<Edge> edges = new ArrayList<>();
        for(int i = 0; i < path.size()-1; i=i+2) {
            edges.add(new Edge(path.get(i), path.get(i+1)));
        }
        
        for(Edge edge : edges) {
            drawPath(edge);
        }
    }

    public void drawPath(Edge edge) {
        g.setColor(parseColor("#FF0000"));
        drawBoldEdge(edge);
    }

    public void drawHoveredEdge(Edge edge) {
        g.setColor(parseColor("#000000"));
        drawBoldEdge(edge);
    }

    private void drawBoldEdge(Edge edge){
        Point from = edge.getNodeOne().getCoord();
        Point to = edge.getNodeTwo().getCoord();
        g.setStroke(new BasicStroke(8));
        g.drawLine(from.x, from.y, to.x, to.y);
        int x = (from.x + to.x)/2;
        int y = (from.y + to.y)/2;

        int rad = 13;
        g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
    }

    public void drawEdge(Edge edge) {
        g.setColor(parseColor("#000000"));
        drawBaseEdge(edge);
        drawWeight(edge);
    }

    private void drawBaseEdge(Edge edge){
        Point from = edge.getNodeOne().getCoord();
        Point to = edge.getNodeTwo().getCoord();
        g.setStroke(new BasicStroke(3));
        g.drawLine(from.x, from.y, to.x, to.y);
    }

    public void drawHalo(Node node){
        g.setColor(parseColor("#E91E63"));
        radius+=5;
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);
        radius-=5;
    }

    public void drawSourceNode(Node node){
        g.setColor(parseColor("#008000"));
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);

        radius-=5;
        g.setColor(parseColor("#FFFFFF"));
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);

        radius+=5;
        g.setColor(parseColor("#FFFFFF"));
        drawCentreText(String.valueOf(node.getId()), node.getX(), node.getY());
    }

    public void drawDestinationNode(Node node){
        g.setColor(parseColor("#FF0000"));
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);

        radius-=5;
        g.setColor(parseColor("#FFFFFF"));
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);

        radius+=5;
        g.setColor(parseColor("#FFFFFF"));
        drawCentreText(String.valueOf(node.getId()), node.getX(), node.getY());
    }

    public void drawNode(Node node){
        g.setColor(parseColor("#000000"));
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);

        radius-=5;
        g.setColor(parseColor("#FFFFFF"));
        g.fillOval(node.getX() - radius, node.getY() - radius, 2 * radius, 2 * radius);

        radius+=5;
        g.setColor(parseColor("#FFFFFF"));
        drawCentreText(String.valueOf(node.getId()), node.getX(), node.getY());
    }

    public void drawWeightText(String text, int x, int y) {
        g.setColor(parseColor("#FFFFFF"));
        FontMetrics fm = g.getFontMetrics();
        double t_width = fm.getStringBounds(text, g).getWidth();
        g.drawString(text, (int) (x - t_width / 2), (y + fm.getMaxAscent() / 2));
    }

    public void drawCentreText(String text, int x, int y) {
        g.setColor(parseColor("#000000"));
        FontMetrics fm = g.getFontMetrics();
        double t_width = fm.getStringBounds(text, g).getWidth();
        g.drawString(text, (int) (x - t_width / 2), (y + fm.getMaxAscent() / 2));
    }


    // Calculations
    private static int sqr(int x) {
        return x * x;
    }
    private static int dist2(Point v, Point w) {
        return sqr(v.x - w.x) + sqr(v.y - w.y);
    }
    private static int distToSegmentSquared(Point p, Point v, Point w) {
        double l2 = dist2(v, w);
        if (l2 == 0) return dist2(p, v);
        double t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
        if (t < 0) return dist2(p, v);
        if (t > 1) return dist2(p, w);
        return dist2(p, new Point(
                (int)(v.x + t * (w.x - v.x)),
                (int)(v.y + t * (w.y - v.y))
        ));
    }
    private static int distToSegment(Point p, Point v, Point w) {
        return (int) Math.sqrt(distToSegmentSquared(p, v, w));
    }

}

class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {

    private DrawUtils drawUtils;

    private Graph graph;

    private Node selectedNode = null;
    private Node hoveredNode = null;
    private Edge hoveredEdge = null;

    private java.util.List<Node> path = null;

    private Point cursor;

    public GraphPanel(Graph graph){
        this.graph = graph;

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setPath(java.util.List<Node> path) {
        this.path = path;
        hoveredEdge = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawUtils = new DrawUtils(graphics2d);

        if(graph.isSolved()){
            drawUtils.drawPath(path);
        }

        if(selectedNode != null && cursor != null){
            Edge e = new Edge(selectedNode, new Node(cursor));
            drawUtils.drawEdge(e);
        }

        for(Edge edge : graph.getEdges()){
            if(edge == hoveredEdge)
                drawUtils.drawHoveredEdge(edge);
            drawUtils.drawEdge(edge);
        }

        for(Node node : graph.getNodes()){
            if(node == selectedNode || node == hoveredNode)
                drawUtils.drawHalo(node);
            if(graph.isSource(node))
                drawUtils.drawSourceNode(node);
            else if(graph.isDestination(node))
                drawUtils.drawDestinationNode(node);
            else
                drawUtils.drawNode(node);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {

        Node selected = null;
        for(Node node : graph.getNodes()) {
            if(DrawUtils.isWithinBounds(e, node.getCoord())){
                selected = node;
                break;
            }
            
        }
        if(hoveredEdge!=null){
            String input = JOptionPane.showInputDialog("Enter weight for " + hoveredEdge.toString() + " : ");
            try {
                int weight = Integer.parseInt(input);
                if (weight > 0) {
                    hoveredEdge.setWeight(weight);
                    graph.setSolved(false);
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "Weight should be positive");
                }
            } catch (NumberFormatException nfe) {}
            return;
        }

        graph.addNode(e.getPoint());
        graph.setSolved(false);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (Node node : graph.getNodes()) {
            if(selectedNode !=null && node!= selectedNode && DrawUtils.isWithinBounds(e, node.getCoord())){
                Edge new_edge = new Edge(selectedNode, node);
                graph.addEdge(new_edge);
                graph.setSolved(false);
            }
        }
        selectedNode = null;
        hoveredNode = null;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        hoveredNode = null;

        for (Node node : graph.getNodes()) {
            if(selectedNode ==null && DrawUtils.isWithinBounds(e, node.getCoord())){
                selectedNode = node;
            } else if(DrawUtils.isWithinBounds(e, node.getCoord())) {
                hoveredNode = node;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoveredEdge = null;
        for (Edge edge : graph.getEdges()) {
            if(DrawUtils.isOnEdge(e, edge)) {
                hoveredEdge = edge;
            }
        }

        repaint();
    }

    public void reset(){
        graph.clear();
        selectedNode = null;
        hoveredNode = null;
        hoveredEdge = null;
        repaint();
    }
}

class MainWindow extends JPanel {

    private Graph graph;
    private GraphPanel graphPanel;
    
    public MainWindow(){
        super.setLayout(new BorderLayout());
        setGraphPanel();
    }

    private void setGraphPanel(){
        
        graph = new Graph();
        graphPanel = new GraphPanel(graph);
        graphPanel.setPreferredSize(new Dimension(480, 480));
        Color c = new Color(255, 255, 255);
        graphPanel.setBackground(c);
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(graphPanel);
        scroll.setPreferredSize(new Dimension(750, 500));
        scroll.getViewport().setViewPosition(new Point(4100, 0));
        add(scroll, BorderLayout.CENTER);
        graphPanel.setLayout(new GridLayout(1, 1));
         JLabel l1 = new JLabel("<html><font size=\"10\">GRAPH PANEL</font></html>", SwingConstants.CENTER);
         l1.setVerticalAlignment(SwingConstants.TOP);
        graphPanel.add(l1);
        setButtons();
    }
    private void setButtons(){
        Button edge = new Button("Add an EDGE");
        Button run = new Button("RUN");
        Button reset = new Button("RESET");
        final Button exit = new Button("EXIT");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DrawUtils.parseColor("#FFFFFF"));
        buttonPanel.add(edge);
        buttonPanel.add(reset);
        buttonPanel.add(run);
        buttonPanel.add(exit);

        edge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            JTextField node1 = new JTextField();
            JTextField node2 = new JTextField();
            Object[] message = {
                "Enter nodes",
                "Node 1:", node1,
                "Node 2:", node2
            };
            int input1= JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
            Node nodee1=null;
            Node nodee2=null;
            for(Node node : graph.getNodes()) {
                if(Integer.parseInt(node1.getText())==node.getId()){
                    nodee1=node;
                }
                if(Integer.parseInt(node2.getText())==node.getId()){
                    nodee2=node;
                }
            }
            Edge new_edge = new Edge(nodee1, nodee2);
                graph.addEdge(new_edge);
                graph.setSolved(false);
            }
        });
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.reset();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrimAlgorithm dijkstraAlgorithm = new PrimAlgorithm(graph);
                try{
                    dijkstraAlgorithm.run();
                    graphPanel.setPath(dijkstraAlgorithm.getDestinationPath());
                } catch (IllegalStateException ise){
                    JOptionPane.showMessageDialog(null, ise.getMessage());
                }
            }
        });
                
           
                
        add(buttonPanel, BorderLayout.SOUTH);
    }

}

class Edge {
    private Node one;
    private Node two;
    private int weight = 1;

    public Edge(Node one, Node two){
        this.one = one;
        this.two = two;
    }

    public Node getNodeOne(){
        return one;
    }

    public Node getNodeTwo(){
        return two;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public int getWeight(){
        return weight;
    }

    public boolean hasNode(Node node){
        return one==node || two==node;
    }

    public boolean equals(Edge edge) {
        return (one ==edge.one && two ==edge.two) || (one ==edge.two && two ==edge.one) ;
    }

    @Override
    public String toString() {
        return "Edge : "
                + getNodeOne().getId() + " - "
                + getNodeTwo().getId();
    }
}

class Graph {
    private int count = 1;
    private java.util.List<Node> nodes = new ArrayList<>();
    private java.util.List<Edge> edges = new ArrayList<>();
   
    private Node source;
    private Node destination;

    private boolean solved = false;
    
    
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setNodes(java.util.List<Node> nodes){
        this.nodes = nodes;
    }

    public java.util.List<Node> getNodes(){
        return nodes;
    }

    public void setEdges(java.util.List<Edge> edges){
        this.edges = edges;
    }

    public java.util.List<Edge> getEdges(){
        return edges;
    }

    public boolean isNodeReachable(Node node){
        for(Edge edge : edges)
            if(node == edge.getNodeOne() || node == edge.getNodeTwo())
                return true;

        return false;
    }

    public void setSource(Node node){
        if(nodes.contains(node))
            source = node;
    }

    public void setDestination(Node node){
        if(nodes.contains(node))
            destination = node;
    }

    public Node getSource(){
        return source;
    }

    public Node getDestination(){
        return destination;
    }

    public boolean isSource(Node node){
        return node == source;
    }

    public boolean isDestination(Node node){
        return node == destination;
    }

    public void addNode(Point coord){
        Node node = new Node(coord);
        addNode(node);
    }

    public void addNode(Node node){
        node.setId(count++);
        nodes.add(node);
        if(node.getId()==1)
            source = node;
    }

    public void addEdge(Edge new_edge){
        boolean added = false;
        for(Edge edge : edges){
            if(edge.equals(new_edge)){
                added = true;
                break;
            }
        }
        if(!added)
            edges.add(new_edge);
    }

    public void deleteNode(Node node){
        java.util.List<Edge> delete = new ArrayList<>();
        for (Edge edge : edges){
            if(edge.hasNode(node)){
                delete.add(edge);
            }
        }
        for (Edge edge : delete){
            edges.remove(edge);
        }
       nodes.remove(node);
    }

    public void clear(){
        count = 1;
        nodes.clear();
        edges.clear();
        solved = false;

        source = null;
        destination = null;
    }

}

class Node {
    private Point coord = new Point();
    private int id;
    private java.util.List<Node> path;

    public Node(){}

    public Node(int id){
        this.id = id;
    }

    public Node(Point p){
        this.coord = p;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCoord(int x, int y){
        coord.setLocation(x, y);
    }

    public Point getCoord(){
        return coord;
    }

    public void setPath(java.util.List<Node> path) {
        this.path = path;
    }

    public java.util.List<Node> getPath() {
        return path;
    }

    public int getX(){
        return (int) coord.getX();
    }

    public int getY(){
        return (int) coord.getY();
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString() {
        return "Node " + id;
    }
}
