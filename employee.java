package TCP;

import java.sql.*;

public class employee()
{
	private String name;
	private String
	
	public void Emloyee()
	{
		
	}
}



public void findBusinessLocation(String businessName, String buildingAddress,
                                 String stateName) {
  PreparedStatement getLocations = connection.prepareStatement(
    "select l.id " +
    "  from BusinessLocation l, Business b, Building g, State s " +
    " where l.partOf = b.id " +
    "   and b.name = ? " +
    "   and l.locatedAt = g.id " +
    "   and g.address = ? " +
    "   and g.partOf = s.id " +
    "   and s.name = ? ");
  PreparedStatement getConnections = connection.prepareStatement(
    "select s.id, n.id, c.usage, n.capacity, n.cost, n.type " +
    "  from Connection c, Network n, State s " +
    " where c.connectedTo = ? " +
    "   and c.connectedBy = n.id " +
    "   and n.partOf = s.id ");
  PreparedStatement getOtherNetworks = connection.prepareStatement(
    "select n.id " +
    "  from State s, Network n " +
    " where n.partOf = s.id " +
    "   and n.id != ? " +
    "   and s.id = ? " +
    "   and n.capacity >= ? " +
    "   and n.type = ? ");

  getLocations.setString(1, businessName);
  getLocations.setString(2, buildingAddress);
  getLocations.setString(3, stateName);
  ResultSet locations = getLocations.executeQuery();
  while (locations.next()) {
    System.out.println("Location Connections");
    getConnections.setInt(1, locations.getInt(1));
    ResultSet connections = getConnections.excuteQuery();
    while (connections.next()) {
      int state = connections.getInt(1);
      int network = connections.getInt(2);
      double usage = connections.getDouble(3);
      double capacity = connections.getDouble(4);
      double cost = connections.getDouble(5);
      String type = connections.getString(6);
      System.out.println("  " + usage + " " + capacity +
                         " " + cost + " " + type);
      getOtherNetworks.setInt(network);
      getOtherNetworks.setInt(state);
      getOtherNetworks.setDouble(usage);
      getOtherNetworks.setString(type);
      ResultSet otherNetworks = getOtherNetworks.executeQuery();
      while (otherNetworks.next()) {
        System.out.println("    Other Network: " + otherNetworks.getInt(1));
      }
      otherNetworks.close();
    }
    connections.close();
  }
  locations.close();
  getOtherNetworks.close();
  getConnections.close();
  getLocations.close();
}
