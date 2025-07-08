# Quantum Bookstore System

A Java-based online bookstore management system that supports different types of books with inventory management, purchasing, and automatic outdated book removal.

## Features

### Book Types
- **Paper Books**: Physical books with stock management and shipping
- **E-Books**: Digital books with file type specification and email delivery
- **Showcase Books**: Demo books that are not available for purchase

### Core Functionality
- Add books to inventory with validation
- Remove outdated books based on publication year
- Purchase books with quantity management
- Automatic stock reduction for paper books
- Integration with shipping and email services

## Class Structure

### Abstract Base Class
```java
abstract class Book
```
- Contains common properties: ISBN, title, price, year published
- Provides validation for price and publication year
- Abstract methods for purchase availability and processing

### Concrete Book Classes

#### PaperBook
- Extends `Book`
- Additional properties: stock quantity
- Stock management with validation
- Integrates with `ShippingService` for delivery

#### EBook
- Extends `Book`
- Additional properties: file type (PDF, DOCS, etc.)
- Always available for purchase
- Integrates with `MailService` for email delivery

#### ShowcaseBook
- Extends `Book`
- Not available for purchase
- Throws exception when purchase is attempted

### BookStore Class
- Manages inventory using HashMap with ISBN as key
- Provides methods for adding, removing, and purchasing books
- Handles outdated book removal with configurable year threshold

### Service Classes
- **ShippingService**: Handles physical book shipping
- **MailService**: Handles digital book email delivery

## Usage Examples

### Adding Books to Inventory
```java
BookStore bookstore = new BookStore();

// Add a paper book
bookstore.addBook(new PaperBook("ID1", "Book1", 45.50d, 2020, 10));

// Add an e-book
bookstore.addBook(new EBook("ID2", "Book2", 30.0d, 2023, "PDF"));

// Add a showcase book
bookstore.addBook(new ShowcaseBook("ID3", "Book3", 100.0d, 2024));
```

### Purchasing Books
```java
// Buy paper books (reduces stock)
double amount = bookstore.buyBook("ID1", 2, "test@email.com", "Giza");

// Buy e-books (always available)
double amount = bookstore.buyBook("ID2", 1, "test@email.com", "Alex");
```

### Removing Outdated Books
```java
// Remove books older than 5 years
List<Book> removedBooks = bookstore.removeOutDatedBooks(5);
System.out.println("Removed " + removedBooks.size() + " outdated books");
```

## Error Handling

The system includes comprehensive error handling for:

### Book Creation Validation
- Price must be positive
- Publication year cannot be in the future
- Paper book stock must be positive
- Duplicate ISBN detection

### Purchase Validation
- Book must exist in inventory
- Sufficient stock for paper books
- Showcase books cannot be purchased

### Common Exceptions
- `IllegalArgumentException`: Invalid input parameters
- `UnsupportedOperationException`: Unsupported operations (e.g., buying showcase books)

## Testing

The `BookstoreTest` class demonstrates:
1. Adding different types of books
2. Successful and failed purchase attempts
3. Stock management
4. Outdated book removal

### Running the Tests
```bash
javac BookstoreTest.java
java BookstoreTest
```

Expected output includes:
- Book addition confirmations
- Purchase transactions with shipping/email notifications
- Error messages for invalid operations
- Outdated book removal results

## Design Patterns

### Inheritance
- All book types inherit from abstract `Book` class
- Common behavior in base class, specific implementations in subclasses

### Polymorphism
- Different book types handle purchases differently
- Uniform interface through abstract methods

### Template Method
- Base class defines structure, subclasses implement details
- Consistent behavior across all book types