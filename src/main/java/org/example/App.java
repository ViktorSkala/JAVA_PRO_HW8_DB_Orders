package org.example;

import org.hibernate.internal.build.AllowSysOut;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.*;

public class App {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPA_Order");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: create client");
                    System.out.println("2: create product");
                    System.out.println("3: insert random clients");
                    System.out.println("4: insert random products");
                    System.out.println("5: insert new order");
                    System.out.println("7: view all clients");
                    System.out.println("8: view all products");
                    System.out.println("9: view all orders");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            insertClient(sc);
                            break;
                        case "2":
                            insertProduct(sc);
                            break;
                        case "3":
                            insertRandomClients(sc);
                            break;
                        case "4":
                            insertRandomProducts(sc);
                            break;
                        case "5":
                            insertNewOrder(sc);
                            break;
                        case "7":
                            viewClients(sc);
                            break;
                        case "8":
                            viewProducts(sc);
                            break;
                        case "9":
                            viewOrders(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void insertClient(Scanner sc) {
        System.out.print("Enter Client first name: ");
        String name = sc.nextLine();
        System.out.print("Enter Client last name: ");
        String lastName = sc.nextLine();

        em.getTransaction().begin();
        try {
            Client client = new Client(name, lastName);
            em.persist(client);
            em.getTransaction().commit();

            System.out.println(client.getId());
        } catch (Exception ex) {
            System.out.println("problem");
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    private static void insertProduct(Scanner sc) {
        System.out.print("Enter product name: ");
        String name = sc.nextLine();
        System.out.print("Enter product price: ");
        String sPrice = sc.nextLine();
        int price = Integer.parseInt(sPrice);

        em.getTransaction().begin();
        try {
            Product product = new Product(name, price);
            em.persist(product);
            em.getTransaction().commit();

            System.out.println(product.getId());
        } catch (Exception ex) {
            System.out.println("problem");
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    private static void insertNewOrder(Scanner sc) {
        System.out.print("Enter Client first name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter Client last name: ");
        String lastName = sc.nextLine();
        System.out.print("Enter amount of positions in your order: ");
        String sProductAmount = sc.nextLine();
        int productAmount = Integer.parseInt(sProductAmount);

        em.getTransaction().begin();
        try {
            Client client = null;
            try {
                Query query = em.createQuery("SELECT c FROM Client c WHERE c.firstName='" + firstName + "' AND c.lastName='" + lastName +"'", Client.class);
                client = (Client) query.getSingleResult();
            } catch (Exception e) {
                System.out.println("No result or not single result");;
            }
            Set<Product> productSet = new HashSet<>();
            for (int i = 0; i < productAmount; i++) {
                System.out.print("Enter product name: ");
                String productName = sc.nextLine();
                try {
                    Query query = em.createQuery("SELECT p FROM Product p WHERE p.name='" + productName + "'", Product.class);
                    Product product = (Product) query.getSingleResult();
                    productSet.add(product);
                } catch (Exception e) {
                    System.out.println("No result or not single result");
                    ;
                }
            }

            Order order = new Order(client);
            order.setProductList(productSet);
            client.getOrderList().add(order);

            em.persist(order);
            em.getTransaction().commit();

            System.out.println(order.getId());
        } catch (Exception ex) {
            System.out.println("problem");
            em.getTransaction().rollback();
            ex.printStackTrace();
        }

    }

    private static void viewClients(Scanner sc) {
        Query query = em.createQuery("SELECT c FROM Client c", Client.class);
        List<Client> clientList = (List<Client>) query.getResultList();
        System.out.println("view list of clients:");

        for (Client cl : clientList) {
            System.out.println(cl);
        }
    }

    private static void viewProducts (Scanner sc){
        Query query = em.createQuery("SELECT p FROM Product p", Product.class);
        List<Product> productList = (List<Product>) query.getResultList();
        System.out.println("view list of products:");

        for (Product pr : productList) {
            System.out.println(pr);
        }
    }

    private static void viewOrders (Scanner sc){
        Query query = em.createQuery("SELECT o FROM Order o", Order.class);
        List<Order> orderList = (List<Order>) query.getResultList();
        System.out.println("view list of orders:");

        for (Order or : orderList) {
            System.out.println(or);
        }
    }

    private static void insertRandomClients(Scanner sc) {
        System.out.print("Enter Clients count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);

        em.getTransaction().begin();
        try {
            for (int i = 0; i < count; i++) {
                Client client = new Client(randomFirstNames(), randomLastNames());
                em.persist(client);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void insertRandomProducts(Scanner sc) {
        System.out.print("Enter products count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);

        em.getTransaction().begin();
        try {
            for (int i = 0; i < count; i++) {
                Product product = new Product(randomProdNames(), RND.nextInt(10));
                em.persist(product);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }
    static final String[] FIRSTNAMES = {"Petr", "Vlad", "Viktor"};
    static final String[] LASTNAMES = {"Pupkin", "Dudkin", "Bubkin"};
    static final String[] PRODNAMES = {"bike", "avto", "ski"};

    static final Random RND = new Random();
    static String randomFirstNames() { return FIRSTNAMES[RND.nextInt(FIRSTNAMES.length)];}

    static String randomLastNames() { return LASTNAMES[RND.nextInt(LASTNAMES.length)];}

    static String randomProdNames() { return PRODNAMES[RND.nextInt(PRODNAMES.length)];}

}
