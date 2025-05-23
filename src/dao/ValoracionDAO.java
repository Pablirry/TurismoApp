package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConexionDB;
import model.ValoracionRuta;
import model.ValoracionEvento;
import model.ValoracionRestaurante;

/**
 * @author Pablo
 */


public class ValoracionDAO {

    /**
     * Método para registrar una valoración de una ruta
     * 
     * @param valoracion : ValoracionRuta
     * @return : boolean
     * @throws ClassNotFoundException
     */

    public boolean registrarValoracionRuta(ValoracionRuta valoracion) throws ClassNotFoundException {
        String sql = "INSERT INTO valoraciones_rutas (id_usuario, id_ruta, puntuacion, comentario) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valoracion.getIdUsuario());
            ps.setInt(2, valoracion.getIdRuta());
            ps.setInt(3, valoracion.getPuntuacion());
            ps.setString(4, valoracion.getComentario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrarValoracionRuta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para registrar una valoración de un restaurante
     * 
     * @param valoracion : ValoracionRestaurante
     * @return : boolean
     * @throws ClassNotFoundException
     */

    public boolean registrarValoracionRestaurante(ValoracionRestaurante valoracion) throws ClassNotFoundException {
        String sql = "INSERT INTO valoraciones_restaurantes (id_usuario, id_restaurante, puntuacion, comentario) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valoracion.getIdUsuario());
            ps.setInt(2, valoracion.getIdRestaurante());
            ps.setInt(3, valoracion.getPuntuacion());
            ps.setString(4, valoracion.getComentario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrarValoracionRestaurante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para obtener todas las valoraciones de restaurantes
     * 
     * @return : list<ValoracionRestaurante>
     * @throws ClassNotFoundException
     */

    public List<ValoracionRestaurante> obtenerTodasValoracionesRestaurante() throws ClassNotFoundException {
        List<ValoracionRestaurante> lista = new ArrayList<>();
        String sql = "SELECT * FROM valoraciones_restaurantes";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ValoracionRestaurante(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_restaurante"),
                        rs.getInt("puntuacion"),
                        rs.getString("comentario")));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodasValoracionesRestaurante: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Método para obtener todas las valoraciones de rutas
     * 
     * @return : list<ValoracionRuta>
     * @throws ClassNotFoundException
     */

    public List<ValoracionRuta> obtenerTodasValoracionesRuta() throws ClassNotFoundException {
        List<ValoracionRuta> lista = new ArrayList<>();
        String sql = "SELECT * FROM valoraciones_rutas";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ValoracionRuta(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_ruta"),
                        rs.getInt("puntuacion"),
                        rs.getString("comentario")));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodasValoracionesRuta: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Método para eliminar una valoración de un restaurante
     * 
     * @param id : entero
     * @return : boolean
     * @throws ClassNotFoundException
     */

    public boolean eliminarValoracionRestaurante(int id) throws ClassNotFoundException {
        String sql = "DELETE FROM valoraciones_restaurantes WHERE id = ?";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarValoracionRestaurante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para eliminar una valoración de una ruta
     * 
     * @param id : entero
     * @return : boolean
     * @throws ClassNotFoundException
     */

    public boolean eliminarValoracionRuta(int id) throws ClassNotFoundException {
        String sql = "DELETE FROM valoraciones_rutas WHERE id = ?";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarValoracionRuta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para obtener la valoración media de una ruta
     * 
     * @param idRuta : entero
     * @return : double
     * @throws ClassNotFoundException
     */

    public double obtenerValoracionMediaRuta(int idRuta) throws ClassNotFoundException {
        String sql = "SELECT AVG(puntuacion) AS media FROM valoraciones_rutas WHERE id_ruta = ?";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRuta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("media");
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerValoracionMediaRuta: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Método para obtener la valoración media de un restaurante
     * 
     * @param idRestaurante : entero
     * @return : double
     * @throws ClassNotFoundException
     */

    public double obtenerValoracionMediaRestaurante(int idRestaurante) throws ClassNotFoundException {
        String sql = "SELECT AVG(puntuacion) AS media FROM valoraciones_restaurantes WHERE id_restaurante = ?";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRestaurante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("media");
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerValoracionMediaRestaurante: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Método para registrar una valoración de un evento
     * 
     * @param valoracion : ValoracionEvento
     * @return : boolean
     * @throws ClassNotFoundException
     */

    public boolean registrarValoracionEvento(ValoracionEvento valoracion) throws ClassNotFoundException {
        String sql = "INSERT INTO valoraciones_eventos (id_usuario, id_evento, puntuacion, comentario) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valoracion.getIdUsuario());
            ps.setInt(2, valoracion.getIdEvento());
            ps.setInt(3, valoracion.getPuntuacion());
            ps.setString(4, valoracion.getComentario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrarValoracionEvento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para obtener todas las valoraciones de eventos
     * 
     * @return : list<ValoracionEvento>
     * @throws ClassNotFoundException
     */

    public java.util.List<ValoracionEvento> obtenerTodasValoracionesEvento() throws ClassNotFoundException {
        List<ValoracionEvento> lista = new ArrayList<>();
        String sql = "SELECT * FROM valoraciones_eventos";
        try (Connection con = ConexionDB.getConection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ValoracionEvento(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_evento"),
                        rs.getInt("puntuacion"),
                        rs.getString("comentario")));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodasValoracionesEvento: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Método para eliminar una valoración de un evento
     * 
     * @param id : entero
     * @return : boolean
     * @throws ClassNotFoundException
     */

    public boolean eliminarValoracionEvento(int id) throws ClassNotFoundException {
        String sql = "DELETE FROM valoraciones_eventos WHERE id = ?";
        try (Connection con = ConexionDB.getConection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarValoracionEvento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para obtener la valoración media de un evento
     * 
     * @param idEvento : entero
     * @return : double
     * @throws ClassNotFoundException
     */

    public double obtenerValoracionMediaEvento(int idEvento) throws ClassNotFoundException {
        String sql = "SELECT AVG(puntuacion) AS media FROM valoraciones_eventos WHERE id_evento = ?";
        try (Connection con = ConexionDB.getConection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("media");
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerValoracionMediaEvento: " + e.getMessage());
        }
        return 0.0;
    }

}
