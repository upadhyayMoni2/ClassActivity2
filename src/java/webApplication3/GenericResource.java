/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webApplication3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.sql.Statement;

@Path("mobile")
public class GenericResource {

    Statement stm = null;
    ResultSet rs = null;
    String id, regionName;
    JSONObject mainObj = new JSONObject();
    JSONArray mainarrList = new JSONArray();
    JSONObject singleobjList = new JSONObject();
    JSONObject singleList = new JSONObject();
    JSONObject singleInsert = new JSONObject();
    JSONObject singleDelete = new JSONObject();
    JSONObject singleUpdate = new JSONObject();
    JSONObject singleConnError = new JSONObject();
    JSONObject singleStmError = new JSONObject();
    JSONObject singleRsError = new JSONObject();


    @Context
    private UriInfo context;

    public GenericResource() {
    }

    @GET
    @Produces("application/xml")
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }
    static Connection conn = null;

    public Connection getConnection() {

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "hr", "inf5180");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    @GET
    @Path("getAllList")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText1() {

        conn = getConnection();
        if (conn != null) {
            try {
                String sql = "select region_id, region_name from regions";
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                rs = stm.executeQuery(sql);
                mainObj.accumulate("Status", "OK");
                mainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                while (rs.next()) {

                    id = rs.getString("region_id");
                    regionName = rs.getString("region_name");
                    singleobjList.accumulate("id", id);
                    singleobjList.accumulate("regionName", regionName);

                    mainarrList.add(singleobjList);

                    singleobjList.clear();
                }
                mainObj.accumulate("Message", mainarrList);
            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            mainObj.accumulate("Status", "Error");
            mainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
            mainObj.accumulate("Message", "Connection Error");
        }

        return mainObj.toString();
    }

    @GET
    @Path("getSingleList&{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText2(@PathParam("id") int regionId) {

        conn = getConnection();
        String sql = "select * from regions where region_id='" + regionId + "'";
        if (conn != null) {
            try {
                stm = conn.createStatement();
                rs = stm.executeQuery(sql);

                if (rs.next() == false) {

                    singleList.accumulate("Status", "Error");
                    singleList.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
                    singleList.accumulate("MEssage", "Record Not Found");
                } else {

                    singleList.accumulate("Status", "OK");
                    singleList.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                    do {

                        id = rs.getString("region_id");

                        regionName = rs.getString("region_name");
                        singleList.accumulate("id", id);
                        singleList.accumulate("regionName", regionName);
                    } while (rs.next());

                }

                System.out.println(singleList);

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            singleList.accumulate("Status", "Error");
            singleList.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            singleList.accumulate("Message", "ConnetionErrr");

        }
        return singleList.toString();
    }

    @GET
    @Path("insertMethod&{id}&{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText3(@PathParam("id") int regionId, @PathParam("name") String regionName) {

        conn = getConnection();

        if (conn != null) {
            String sql = "insert into regions(region_id , region_name) values('" + regionId + "','" + regionName + "') ";
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                if (i > 0) {

                    singleInsert.accumulate("Status", "OK");
                    singleInsert.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
                    singleInsert.accumulate("message", "Record inserted");
                    System.out.println(singleInsert);

                } else {
                    singleInsert.accumulate("message", "Record Not inserted");
                    System.out.println(singleInsert);
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            singleInsert.accumulate("Status", "Error");
            singleInsert.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            singleInsert.accumulate("message", "ConnectionError");
        }
        return singleInsert.toString();
    }

    @GET
    @Path("deleteMethod&{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText4(@PathParam("id") int regionId) {

        conn = getConnection();

        if (conn != null) {
            String sql = "Delete from regions where region_id = '" + regionId + "'";

            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);
                if (i > 0) {
                    singleDelete.accumulate("Status", "OK");
                    singleDelete.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                    singleDelete.accumulate("message", "Record Deleted");
                    System.out.println(singleDelete);
                } else {
                    singleDelete.accumulate("message", "Record not Deleted");
                    System.out.println(singleDelete);
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            singleDelete.accumulate("Status", "Error");
            singleDelete.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            singleDelete.accumulate("message", "ConnectionEroor");
        }
        return singleDelete.toString();
    }

    @GET
    @Path("updateMethod&{id}&{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText5(@PathParam("id") int regionId, @PathParam("name") String regionName) {

        conn = getConnection();
        if (conn != null) {
            String sql = "update regions set region_name='" + regionName + "' where region_id= '" + regionId + "'";

            try {
                stm = conn.createStatement();

                int i = stm.executeUpdate(sql);

                if (i > 0) {

                    singleUpdate.accumulate("Status", "OK");
                    singleUpdate.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                    singleUpdate.accumulate("message", "Record Updated");
                    System.out.println(singleUpdate);

                } else {
                    singleUpdate.accumulate("message", "Record Not Updated");
                    System.out.println(singleUpdate);
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            singleUpdate.accumulate("Status", "Error");
            singleUpdate.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            singleUpdate.accumulate("message", "ConnectionError");
        }
        return singleUpdate.toString();
    }

    public void closeConnection() {


        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                singleRsError.accumulate("Status", "Error");
                singleRsError.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
                singleRsError.accumulate("message", "ResultSet error");
                System.out.println(singleRsError);
            }
        }
        if (stm != null) {
            try {
                stm.close();
            } catch (SQLException e) {

                singleStmError.accumulate("message", "Statment error");
                System.out.println(singleStmError);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                singleConnError.accumulate("message", e);
                System.out.println(singleConnError);
            }
        }
    }

}
