import java.time.LocalDate;
import java.util.*;

abstract class Book{
    protected final String ISBN, title;
    protected final double price;
    protected final int yearPublished;
    public Book(String ISBN, String title, double price, int yearPublished){
        this.ISBN = ISBN;
        this.title = title;
        this.price = price;
        this.yearPublished = yearPublished;
        if(price <= 0 || yearPublished > LocalDate.now().getYear())
            throw new IllegalArgumentException("Invalid price or year of publishing");
    }
    public String getISBN() { return this.ISBN; }
    public String getTitle() { return this.title; }
    public double getPrice() { return this.price; }
    public int getYearPublished() { return this.yearPublished; }
    public boolean isOutDated(int passedYears){
        return (LocalDate.now().getYear()-this.yearPublished) >= passedYears;
    }
    public abstract boolean isAvailableForPurchase(int quantity);
    public abstract void purchase(String email, String address, int quantity);
}

class PaperBook extends Book{
    private int stock;
    public PaperBook(String ISBN, String title, double price, int yearPublished, int stock){
        super(ISBN, title, price, yearPublished);
        this.stock = stock;
        if(stock <= 0)
            throw new IllegalArgumentException("Invalid stock");
    }
    public int getStock() { return this.stock; }
    public void decreaseStock(int quantity){
        if(this.stock <= 0){
            throw new IllegalArgumentException(this.title + "is out of stock");
        }
        if(this.stock < quantity)
            throw new IllegalArgumentException("There are not enough available quantity for "+ this.title);
        this.stock -= quantity;
    }
    @Override
    public boolean isAvailableForPurchase(int quantity){
        return this.stock >= quantity;
    }
    @Override
    public void purchase(String email, String address, int quantity){
        ShippingService.ship(this, address, quantity);
    }
}

class EBook extends Book{
    private final String fileType;
    public EBook(String ISBN, String title, double price, int yearPublished, String fileType){
        super(ISBN, title, price, yearPublished);
        this.fileType = fileType;
    }
    public String getFileType() { return this.fileType; }
    @Override
    public boolean isAvailableForPurchase(int quantity) { return true; }
    @Override
    public void purchase(String email, String address, int quantity){
        MailService.sendEmail(this, email, quantity);
    }
}
class ShowcaseBook extends Book{
    public ShowcaseBook(String ISBN, String title, double price, int yearPublished){
        super(ISBN, title, price, yearPublished);
    }
    @Override
    public boolean isAvailableForPurchase(int quantity) { return false; }
    @Override
    public void purchase(String email, String address, int quantity) {
        throw new UnsupportedOperationException("Showcase books are not for sale");
    }
}
class ShippingService {
    public static void ship(PaperBook book, String address, int quantity) {
        System.out.println("Shipping " + quantity + " copies of " + book.getTitle() + " to " + address);
    }
}
class MailService {
    public static void sendEmail(EBook book, String email, int quantity) {
        System.out.println("Sending " + quantity + " files of " + book.getTitle() + " to " + email);
    }
}

class BookStore {
    private Map<String, Book> inventory = new HashMap<>();
    public Map<String, Book> getInventory() { return this.inventory; }
    public void addBook(Book book){
        if(this.inventory.get(book.getISBN()) != null)
            throw new IllegalArgumentException("There is a book with the same ISBN");
        this.inventory.put(book.getISBN(), book); }
    public List<Book> removeOutDatedBooks(int passedYears){
        List<Book> outDatedBooks = new ArrayList<>();
        Iterator<Map.Entry<String, Book>> iterator = inventory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Book> entry = iterator.next();
            Book book = entry.getValue();
            if (book.isOutDated(passedYears)) {
                outDatedBooks.add(book);
                iterator.remove();
            }
        }
        return  outDatedBooks;
    }
    public double buyBook(String ISBN, int quantity, String email, String address){
        Book requiredBook = this.inventory.get(ISBN);
        if(requiredBook == null)
            throw new IllegalArgumentException("The book with identifier " + ISBN + " is not found");
        if(!requiredBook.isAvailableForPurchase(quantity))
            throw new UnsupportedOperationException("This operation can not be done");
        double totalAmount = requiredBook.getPrice() * quantity;
        if(requiredBook instanceof PaperBook){
            ((PaperBook) requiredBook).decreaseStock(quantity);
        }
        requiredBook.purchase(email, address, quantity);
        return totalAmount;
    }
}

class BookstoreTest {
    public static void main(String[] args) {
        BookStore bookstore = new BookStore();

        // Test 1: Adding books
        System.out.println("Adding books to inventory:");
        bookstore.addBook(new PaperBook("ID1", "Book1", 45.50d, 2020, 10));
        bookstore.addBook(new EBook("ID2", "Book2", 30.0d, 2023, "PDF"));
        bookstore.addBook(new ShowcaseBook("ID3", "Book3", 100.0d, 2024));
        bookstore.addBook(new PaperBook("ID4", "Book4", 25.00d, 2010, 5));
        bookstore.addBook(new EBook("ID5", "Book5", 35.50d, 2022, "DOCS"));

        // Test 2: Buying books
        System.out.println("\nBuying books");
        try {
            double amount1 = bookstore.buyBook("ID1", 2, "omar@email.com", "50 Daqqi street");
            System.out.println("Paid amount: $" + amount1);

            double amount2 = bookstore.buyBook("ID2", 1, "ahmed@email.com", "120 Faisal street");
            System.out.println("Paid amount: $" + amount2);

            try {
                // showcase book buying so it will fail and throw exception
                bookstore.buyBook("ID3", 1, "test@email.com", "120 Faisal street");
            } catch (Exception e) {
                System.out.println("Expected error - " + e.getMessage());
            }

            try {
                // buying quantity more stock
                bookstore.buyBook("ID1", 15, "test@email.com", "120 Faisal street");
            } catch (Exception e) {
                System.out.println("Expected error - " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error - " + e.getMessage());
        }

        // Test 3: Removing outdated books
        System.out.println("\nRemoving outdated books (older than 5 years)");
        List<Book> removedBooks = bookstore.removeOutDatedBooks(5);
        System.out.println("Removed " + removedBooks.size() + " outdated books");
    }
}