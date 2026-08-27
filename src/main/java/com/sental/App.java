//package com.sental;
//
//import com.sental.controller.*;
//import com.sental.dto.request.*;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@ComponentScan
//public class App {
//
//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
//
//        UserController userController = ctx.getBean(UserController.class);
//        VenueController venueController = ctx.getBean(VenueController.class);
//        CategoryController categoryController = ctx.getBean(CategoryController.class);
//        OrganizerController organizerController = ctx.getBean(OrganizerController.class);
//        EventController eventController = ctx.getBean(EventController.class);
//        TicketController ticketController = ctx.getBean(TicketController.class);
//
//        demoUser(userController);
//        demoVenue(venueController);
//        demoCategory(categoryController);
//        demoOrganizer(organizerController);
//        demoEvent(eventController);
//        demoTicket(ticketController);
//
//        demoThreads(eventController);
//        demoRollback(eventController);
//
//        ctx.close();
//    }
//
//    private static void demoUser(UserController userController) {
//        System.out.println("User");
//        System.out.println("CREATE USER");
//        UserCreateRequest user1 = new UserCreateRequest();
//        user1.setEmail("ivan123@mail.ru");
//        user1.setUsername("ivan");
//        user1.setPassword("123");
//        String json1 = userController.create(user1);
//        System.out.println("Create user1: " + json1);
//
//        UserCreateRequest user2 = new UserCreateRequest();
//        user2.setEmail("vanya@mail.ru");
//        user2.setUsername("vanya");
//        user2.setPassword("123");
//        String json2 = userController.create(user2);
//        System.out.println("Create user2: " + json2);
//
//        System.out.println("READ (find by id)");
//        String read1 = userController.findById(1L);
//        System.out.println("Read: " + read1);
//
//        System.out.println("DELETE USER");
//        userController.delete(2L);
//        System.out.println("Delete user 2");
//
//        System.out.println("READ (find all)");
//        String read2 = userController.findAll();
//        System.out.println("All: " + read2);
//        System.out.println();
//    }
//
//    private static void demoVenue(VenueController venueController) {
//        System.out.println("VENUE");
//        System.out.println("CREATE VENUE");
//        VenueCreateRequest venue1 = new VenueCreateRequest();
//        venue1.setName("VTB Arena");
//        venue1.setAddress("Ulitsa, 5");
//        venue1.setCity("Moscow");
//        venue1.setRegion("Moscow region");
//        venue1.setCapacity(5000);
//        String json1 = venueController.create(venue1);
//        System.out.println("Create venue1: " + json1);
//
//        VenueCreateRequest venue2 = new VenueCreateRequest();
//        venue2.setName("IT Hall");
//        venue2.setAddress("Drugaia Ulitsa, 6");
//        venue2.setCity("St. Petersburg");
//        venue2.setRegion("Leningrad region");
//        venue2.setCapacity(1000);
//        String json2 = venueController.create(venue2);
//        System.out.println("Create venue2: " + json2);
//
//        System.out.println("READ (find by id)");
//        String read1 = venueController.findById(1L);
//        System.out.println("Read : " + read1);
//
//        System.out.println("DELETE VENUE");
//        venueController.delete(2L);
//        System.out.println("Delete venue 2");
//
//        System.out.println("READ (find all)");
//        String read2 = venueController.findAll();
//        System.out.println("All: " + read2);
//        System.out.println();
//    }
//
//    private static void demoCategory(CategoryController categoryController) {
//        System.out.println("Category");
//        System.out.println("CREATE CATEGORY");
//        CategoryCreateRequest category1 = new CategoryCreateRequest();
//        category1.setName("Music");
//        String json1 = categoryController.create(category1);
//        System.out.println("Create category 1: " + json1);
//
//        CategoryCreateRequest category2 = new CategoryCreateRequest();
//        category2.setName("IT");
//        String json2 = categoryController.create(category2);
//        System.out.println("Create category 2: " + json2);
//
//        System.out.println("READ (find by id)");
//        String read1 = categoryController.findById(1L);
//        System.out.println("Read: " + read1);
//
//        System.out.println("DELETE CATEGORY");
//        categoryController.delete(2L);
//        System.out.println("Delete category 2");
//
//        System.out.println("READ (find all)");
//        String read2 = categoryController.findAll();
//        System.out.println("All: " + read2);
//        System.out.println();
//    }
//
//    private static void demoOrganizer(OrganizerController organizerController) {
//        System.out.println("Organizer");
//        System.out.println("CREATE ORGANIZER");
//        OrganizerCreateRequest organizer1 = new OrganizerCreateRequest();
//        organizer1.setUserId(1L);
//        organizer1.setCompanyName("Organizer1");
//        organizer1.setPhone("+79999999999");
//        String json1 = organizerController.create(organizer1);
//        System.out.println("Create organizer1: " + json1);
//
//        OrganizerCreateRequest organizer2 = new OrganizerCreateRequest();
//        organizer2.setUserId(1L);
//        organizer2.setCompanyName("Organizer2");
//        organizer2.setPhone("+79999999991");
//        String json2 = organizerController.create(organizer2);
//        System.out.println("Create organizer2: " + json2);
//
//        System.out.println("READ (find by id)");
//        String read1 = organizerController.findById(1L);
//        System.out.println("Read: " + read1);
//
//        System.out.println("DELETE ORGANIZER");
//        organizerController.delete(2L);
//        System.out.println("Delete organizer2");
//
//        System.out.println("READ (find all)");
//        String read2 = organizerController.findAll();
//        System.out.println("All: " + read2);
//        System.out.println();
//    }
//
//    private static void demoEvent(EventController eventController) {
//        System.out.println("Event");
//        System.out.println("CREATE EVENT");
//        EventCreateRequest event1 = new EventCreateRequest();
//        event1.setVenueId(1L);
//        event1.setTitle("Java Conference");
//        event1.setDescription("JVM");
//        event1.setCapacity(4000);
//        event1.setPrice(500.0);
//        String json1 = eventController.create(event1);
//        System.out.println("Create event1: " + json1);
//
//        EventCreateRequest event2 = new EventCreateRequest();
//        event2.setVenueId(1L);
//        event2.setTitle("Rock Festival");
//        event2.setDescription("Concert");
//        event2.setCapacity(1000);
//        event2.setPrice(500.0);
//        String json2 = eventController.create(event2);
//        System.out.println("Create event2: " + json2);
//
//        System.out.println("READ (find by id)");
//        String read = eventController.findById(1L);
//        System.out.println("Read: " + read);
//
//        System.out.println("DELETE EVENT");
//        eventController.delete(2L);
//        System.out.println("Delete event 2");
//
//        System.out.println("READ (find all)");
//        String remaining = eventController.findAll();
//        System.out.println("All: " + remaining);
//        System.out.println();
//    }
//
//    private static void demoTicket(TicketController ticketController) {
//        System.out.println("Ticket");
//        System.out.println("CREATE TICKET");
//        TicketCreateRequest ticket1 = new TicketCreateRequest();
//        ticket1.setUserId(1L);
//        ticket1.setEventId(1L);
//        String json1 = ticketController.create(ticket1);
//        System.out.println("Create ticket1: " + json1);
//
//        TicketCreateRequest ticket2 = new TicketCreateRequest();
//        ticket2.setUserId(1L);
//        ticket2.setEventId(1L);
//        String json2 = ticketController.create(ticket2);
//        System.out.println("Create ticket2: " + json2);
//
//        System.out.println("READ (find all)");
//        String read = ticketController.findById(1L);
//        System.out.println("Read: " + read);
//
//        System.out.println("DELETE TICKET");
//        ticketController.delete(2L);
//        System.out.println("Delete ticket 2");
//
//        System.out.println("READ (find all)");
//        String remaining = ticketController.findAll();
//        System.out.println("All: " + remaining);
//        System.out.println();
//    }
//
//    private static void demoThreads(EventController eventController) {
//        System.out.println("THREAD");
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            Thread thread = new Thread(() -> {
//                EventCreateRequest request = EventCreateRequest.builder()
//                        .venueId(1L)
//                        .title("Thread event " + Thread.currentThread().getName())
//                        .description("demo")
//                        .eventDate(LocalDateTime.now())
//                        .capacity(100)
//                        .price(100.0)
//                        .build();
//                String json = eventController.createWithCategories(request, List.of(1L, 2L));
//                System.out.println(Thread.currentThread().getName() + ": " + json);
//            });
//            thread.setName("worker-" + i);
//            threads.add(thread);
//        }
//
//        for (Thread thread : threads) {
//            thread.start();
//        }
//
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//        System.out.println("Thread demo finish");
//    }
//
//    private static void demoRollback(EventController eventController) {
//        System.out.println("ROLLBACK DEMO");
//        EventCreateRequest request = EventCreateRequest.builder()
//                .venueId(1L)
//                .title("Rollback event " + Thread.currentThread().getName())
//                .description("demo")
//                .eventDate(LocalDateTime.now())
//                .capacity(100)
//                .price(100.0)
//                .build();
//        try {
//            eventController.createWithCategories(request, List.of(999L));
//        } catch (RuntimeException e) {
//            System.out.println("Rollback demo: " + e.getMessage());
//        }
//    }
//}