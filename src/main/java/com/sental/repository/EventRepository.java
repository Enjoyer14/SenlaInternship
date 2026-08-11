package com.sental.repository;

import com.sental.database.ConnectionHolder;
import com.sental.entity.Event;
import com.sental.enums.EventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {

    private final ConnectionHolder connectionHolder;

    @Autowired
    public EventRepository(ConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public Optional<Event> findById(Long id) {
        String sql = "SELECT id, venue_id, title, description, event_date, capacity, booked_seats, price, status, created_at, updated_at FROM event WHERE id = ?";
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(toEvent(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("fail to find event by id: " + id, e);
        }
    }

    public List<Event> findAll() {
        String sql = "SELECT id, venue_id, title, description, event_date, capacity, booked_seats, price, status, created_at, updated_at FROM event";
        Connection connection = connectionHolder.getConnection();
        List<Event> events = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                events.add(toEvent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("fail to find all events", e);
        }
        return events;
    }

    public Event save(Event event) {
        if (event.getId() == null) {
            insert(event);
        } else {
            update(event);
        }
        return event;
    }

    private void insert(Event event) {
        String sql = "INSERT INTO event (venue_id, title, description, event_date, capacity, booked_seats, price, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, event.getVenueId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            setTimestamp(stmt, 4, event.getEventDate());
            stmt.setInt(5, event.getCapacity());
            stmt.setInt(6, event.getBookedSeats());
            stmt.setDouble(7, event.getPrice());
            stmt.setString(8, event.getStatus().name());
            setTimestamp(stmt, 9, event.getCreatedAt());
            setTimestamp(stmt, 10, event.getUpdatedAt());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    event.setId(rs.getLong("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("fail to insert event", e);
        }
    }

    private void update(Event event) {
        String sql = "UPDATE event SET venue_id = ?, title = ?, description = ?, event_date = ?, capacity = ?, booked_seats = ?, price = ?, status = ?, updated_at = ? WHERE id = ?";
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, event.getVenueId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            setTimestamp(stmt, 4, event.getEventDate());
            stmt.setInt(5, event.getCapacity());
            stmt.setInt(6, event.getBookedSeats());
            stmt.setDouble(7, event.getPrice());
            stmt.setString(8, event.getStatus().name());
            setTimestamp(stmt, 9, event.getUpdatedAt());
            stmt.setLong(10, event.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("fail to update event: " + event.getId(), e);
        }
    }

    private void setTimestamp(PreparedStatement stmt, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            stmt.setTimestamp(index, null);
        } else {
            stmt.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    public void delete(Long id) {
        deleteBySql("DELETE FROM event_category WHERE event_id = ?", id);
        deleteBySql("DELETE FROM event_organizer WHERE event_id = ?", id);
        deleteBySql("DELETE FROM event WHERE id = ?", id);
    }

    private void deleteBySql(String sql, Long id) {
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("fail to delete event: " + id, e);
        }
    }

    public void addCategory(Long eventId, Long categoryId) {
        String sql = "INSERT INTO event_category (event_id, category_id) VALUES (?, ?)";
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            stmt.setLong(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("fail to add category to event: " + eventId, e);
        }
    }

    private Event toEvent(ResultSet resultSet) throws SQLException {
        return Event.builder()
                .id(resultSet.getLong("id"))
                .venueId(resultSet.getLong("venue_id"))
                .title(resultSet.getString("title"))
                .description(resultSet.getString("description"))
                .eventDate(toLocalDateTime(resultSet.getTimestamp("event_date")))
                .capacity(resultSet.getInt("capacity"))
                .bookedSeats(resultSet.getInt("booked_seats"))
                .price(resultSet.getDouble("price"))
                .status(EventStatus.valueOf(resultSet.getString("status")))
                .createdAt(toLocalDateTime(resultSet.getTimestamp("created_at")))
                .updatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")))
                .build();
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}