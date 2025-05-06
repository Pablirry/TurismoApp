package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConexionDB;
import model.Reserva;

public class ReservarDAO {

    public boolean reservarRuta(Reserva reserva) throws ClassNotFoundException {
        String sql = "INSERT INTO reservas (id_usuario, id_ruta, fecha, confirmada) VALUES (?, ?, ?, ?)";
    try (Connection con = ConexionDB.getConection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, reserva.getIdUsuario());
        ps.setInt(2, reserva.getIdRuta());
        if (reserva.getFecha() != null) {
            ps.setDate(3, new java.sql.Date(reserva.getFecha().getTime()));
        } else {
            ps.setDate(3, null);
        }
        ps.setBoolean(4, reserva.isConfirmada());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error crearReserva: " + e.getMessage());
        return false;
    }
    }

    public boolean cancelarReserva(int idReserva) throws ClassNotFoundException {
        String sql = "DELETE FROM reservas WHERE id = ?";
        try (Connection con = ConexionDB.getConection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelarReserva: " + e.getMessage());
            return false;
        }
    }

    public boolean confirmarReserva(int idReserva) throws ClassNotFoundException {
        String sql = "UPDATE reservas SET confirmada = 1 WHERE id = ?";
        try (Connection con = ConexionDB.getConection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error confirmarReserva: " + e.getMessage());
            return false;
        }
    }
    
    public List<Reserva> obtenerReservasUsuario(int idUsuario) throws ClassNotFoundException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_usuario = ?";
        try (Connection con = ConexionDB.getConection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(new Reserva(
                    rs.getInt("id"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_ruta"),
                    rs.getDate("fecha"),
                    rs.getBoolean("confirmada")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerReservasUsuario: " + e.getMessage());
        }
        return reservas;
    }
}