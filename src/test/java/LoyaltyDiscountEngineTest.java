import cl.ucn.modelo.Customer;
import cl.ucn.modelo.LoyaltyDiscountEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;
import org.junit.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class LoyaltyDiscountEngineTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private LoyaltyDiscountEngine discountEngine;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("shopping");
    }

    @AfterClass
    public static void tearDownClass() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Before
    public void setUp() {
        em = emf.createEntityManager();
        discountEngine = new LoyaltyDiscountEngine(em);
    }

    @After
    public void tearDown() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }



    @Test
    public void testDescuentoNulo(){
        // Cliente básico, sin pedidos, sin antigüedad, sin promo. Esperado: 0.0
        final double ERROR = 1e-9;
        double expected = 0.0;
        String id = "c021";
        LocalDate joinDate = LocalDate.now();
        int orders = 0;
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.BASIC;
        boolean promo = false;
        Customer c = new Customer(id, joinDate, orders, level, promo);

        double discount = discountEngine.computeDiscount(c);

        if(Math.abs(expected-discount) < ERROR){
            discount = expected;
        }

        boolean result = discount == expected;
        assertTrue("resultado: "+ discount ,result);

    }

    @Test
    public void testClienteSilver(){
        // Cliente con nivel SILVER, 3 años, 50 órdenes. Esperado: 0.05
        double expected = 0.05;
        final double ERROR = 1e-9;
        String id = "c021";
        String threeYearsDate = LocalDate.now().toString().replace(  String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getYear()-3) );
        assertTrue(ChronoUnit.YEARS.between(LocalDate.parse(threeYearsDate), LocalDate.now()) == 3);
        LocalDate joinDate =LocalDate.parse(threeYearsDate);
        int orders = 50;
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.SILVER;
        boolean promo = false;
        Customer c = new Customer(id, joinDate, orders, level, promo);
        double discount = discountEngine.computeDiscount(c);

        // solucion.
        if(Math.abs(expected-discount) < ERROR){
            discount = expected;
        }

        boolean result = discount == expected;
        assertTrue("resultado: "+ discount ,result);

    }
    @Test
    public void testAntiguedadMayor5Años(){
        // Cliente GOLD con 6 años, 10 órdenes. Esperado: 0.15
        final double ERROR = 1e-9;
        double expected = 0.15;

        String id = "c021";
        String sixYearsDate = LocalDate.now().toString().replace(  String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getYear()-6) );
        assertTrue(ChronoUnit.YEARS.between(LocalDate.parse(sixYearsDate), LocalDate.now()) == 6);
        LocalDate joinDate =LocalDate.parse(sixYearsDate);
        int orders = 10;
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.GOLD;
        boolean promo = false;
        Customer c = new Customer(id, joinDate, orders, level, promo);
        double discount = discountEngine.computeDiscount(c); // esta retornando overflow decimal point 15.000000000002
        // solucion.
        if(Math.abs(expected-discount) < ERROR){
            discount = expected;
        }

        boolean result = discount == 0.15;
        assertTrue("resultado: "+ discount ,result);
    }
    @Test
    public void testTotalOrdersMayor100(){
        // Cliente GOLD con 101 órdenes. Esperado: 0.15

        final double ERROR = 1e-9;
        double expected = 0.15;

        String id = "c021";
        LocalDate joinDate =LocalDate.now();
        int orders = 101;
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.GOLD;
        boolean promo = false;
        Customer c = new Customer(id, joinDate, orders, level, promo);
        double discount = discountEngine.computeDiscount(c); // esta retornando overflow decimal point 15.000000000002
        // solucion.
        if(Math.abs(expected-discount) < ERROR){
            discount = expected;
        }
        boolean result = (discount == expected);
        assertTrue("resultado: "+ discount ,result);
    }
    @Test
    public void testTienePromoActiva(){
        // Cliente BASIC con promoción activa. Esperado: 0.10

        final double ERROR = 1e-9;
        double expected = 0.10;

        String id = "c021";
        LocalDate joinDate =LocalDate.now();
        // int orders = (new Random()).nextInt(1000);
        int orders = (new Random()).nextInt(100);
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.BASIC;
        boolean promo = true;
        Customer c = new Customer(id, joinDate, orders, level, promo);
        double discount = discountEngine.computeDiscount(c); // esta retornando overflow decimal point 10.000000000002
        // solucion.
        if(Math.abs(expected-discount) < ERROR){
            discount = expected;
        }

        boolean result = (discount == expected);
        assertTrue("resultado: "+ discount ,result);
    }
    @Test
    public void testDescuentoMaximo30Porciento(){
        // Cliente PLATINUM con 10 años, 150 órdenes, promoción activa. Esperado: 0.30
        final double ERROR = 1e-9;
        double expected = 0.30;

        String id = "c021";
        String tenYearsDate = LocalDate.now().toString().replace(  String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getYear()-10) );
        assertTrue(ChronoUnit.YEARS.between(LocalDate.parse(tenYearsDate), LocalDate.now()) == 10);
        LocalDate joinDate =LocalDate.parse(tenYearsDate);
        int orders = 150;
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.PLATINUM;
        boolean promo = true;
        Customer c = new Customer(id, joinDate, orders, level, promo);
        double discount = discountEngine.computeDiscount(c); // esta retornando overflow decimal point 15.000000000002
        // solucion.
        if(Math.abs(expected-discount) < ERROR){
            discount = expected;
        }

        boolean result = discount == expected;
        assertTrue("resultado: "+ discount ,result);

    }
    @Test

    public void testRegistroEnLogs() {
        // Verificar que se registre correctamente una operación en logs.

        String id = "c021";
        long logsBeforeTransaction = discountEngine.countLogsForCustomer(id);

        LocalDate joinDate = LocalDate.now();
        int orders = 0;
        Customer.LoyaltyLevel level = Customer.LoyaltyLevel.BASIC;
        boolean promo = false;
        Customer c = new Customer(id, joinDate, orders, level, promo);

        double discount = discountEngine.computeDiscount(c);

        long newLogs  = discountEngine.countLogsForCustomer(id)- logsBeforeTransaction;

        boolean result = newLogs == 1; // Simular que se registró correctamente;
        assertTrue("El registro en logs falló, logsBeforeTransaction "+logsBeforeTransaction+", log nuevos: "+newLogs, result);


    }

    @Test
    public void testBusquedaPorId() {
        // Usar computeDiscountById() para calcular descuento

        String id = getRandomIdFromDatabase(); /// obtenemos un id aleatorio de la base de datos.

        if(id == null){
            Assert.fail("No se encontró un ID de cliente en la base de datos.");
            return; // Salir del test si no hay ID disponible
        }

        double idDiscount = discountEngine.computeDiscountById(id);

        boolean result =  idDiscount >=0 && idDiscount <= 0.30; // si el descuento está en el rango esperado;

        assertTrue("id "+id+", discount: "+ idDiscount, result);




    }
    @Test (expected = IllegalArgumentException.class)
    public void testExceptionPorNulo(){
        // Llamar a computeDiscount(null) y verificar IllegalArgumentException
        discountEngine.computeDiscount(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionPorIdFaltante(){
        // Cliente sin ID. Esperado: IllegalArgumentException
        discountEngine.computeDiscountById(null);

    }
    public String getRandomIdFromDatabase() {
        String id;
        if(em!= null) {
            id = em.createQuery("SELECT c.id FROM Customer c ORDER BY RANDOM()", String.class)
                    .setMaxResults(1)
                    .getSingleResult();
            return id;
        }
        return null;


    }












}
