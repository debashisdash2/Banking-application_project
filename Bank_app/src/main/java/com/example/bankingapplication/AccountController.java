package com.example.bankingapplication;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankingapplication.repo.AccountRepository;
import com.example.bankingapplication.service.AccountService;
@RestController
public class AccountController {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountService accountService;

	// CREATE ACCOUNT
	@PostMapping("/create")
	public ResponseEntity<?> createAccount(@RequestBody Account account, @RequestParam("age") int age) {
		if (age >= 10) {
			account.setAge(age);
			Account create = this.accountRepository.save(account);
			return ResponseEntity.ok(create);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Age should be minimum 10 years ");
		}
	}

	// GET ALL ACCOUNT
	@GetMapping("/getallaccount")
	public ResponseEntity<?> findAccountDetails() {
		List<Account> all = this.accountRepository.findAll();
		return ResponseEntity.ok(all);
	}

	// GET ACCOUNT BY ID
	@GetMapping("/getaccbyid/{id}")
	public ResponseEntity<?> getAccbyId(@PathVariable("id") int id) {
		Optional<Account> optional = this.accountRepository.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Id");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// GET ACCOUNT BELOW ACCOUNT BALANCE 
	@GetMapping("getbyaccbalanceless")
	public ResponseEntity<?> getbyaccbalance(@RequestParam("accbalance") double accbalance) {
		List<Account> account = accountRepository.findByAccBalanceLessThan(accbalance);
		if (!account.isEmpty()) {
			return ResponseEntity.ok(account);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Account Balance");
		}

	}

	// GET ACCOUNT ABOVE ACCOUNT BALANCE
	@GetMapping("getbyaccbalancegreater")
	public ResponseEntity<?> getbybalance(@RequestParam("accbalance") double accbalance) {
		List<Account> account = accountRepository.findByAccBalanceGreaterThan(accbalance);
		if (!account.isEmpty()) {
			return ResponseEntity.ok(account);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Account Balance");
		}
	}

	// GET ACCOUNT BY NAME
	@GetMapping("/getbyAccHolderName")
	public ResponseEntity<?> getaccbyName(@RequestParam("name") String name) {
		Optional<Account> optional = accountRepository.findByAccHolderName(name);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid AccountHolder Name");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// UPDATE ACCOUNT BY ID
	@PutMapping("updatebyid/{id}")
	public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody Account account) {
		Optional<Account> optional = this.accountRepository.findById(id);
		if (optional.isPresent()) {
			Account a = optional.get();
			a.setAccBalance(account.getAccBalance());
			a.setAccHolderName(account.getAccHolderName());
			a.setAccNumber(account.getAccNumber());
			a.setAddress(account.getAddress());
			this.accountRepository.save(a);
			return ResponseEntity.ok(a);
		}
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Id");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// DELETE ACCOUNT BY ID
	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") int id) {
		Optional<Account> optional = this.accountRepository.findById(id);
		if (optional.isPresent()) {
			this.accountRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid AccountId");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// DEPOSITE AMOUNT BY ID
	@PutMapping("/depositebyid/{id}")
	public ResponseEntity<?> deposite(@PathVariable("id") int id, @RequestBody Deposite deposite) {
		Optional<Account> optional = this.accountRepository.findById(id);
		if (optional.isPresent()) {
			Account account = optional.get();
			account.setAccBalance(account.getAccBalance() + deposite.getAmount());
			this.accountRepository.save(account);
			return ResponseEntity.ok(account);
		}
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Id");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// WITHDRAW AMOUNT BY ID
	@PutMapping("/withdrawbyid/{id}")
	public ResponseEntity<?> withdraw(@PathVariable("id") int id, @RequestBody Withdraw withdraw) {
		Optional<Account> optional = this.accountRepository.findById(id);
		if (optional.isPresent()) {
			Account account = optional.get();
			if (account.getAccBalance() < withdraw.getAmount()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Account Balance");
			}
			account.setAccBalance(account.getAccBalance() - withdraw.getAmount());
			this.accountRepository.save(account);
			return ResponseEntity.ok(account);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid AccountId");
		}
	}

	// CHECK LOAN ELIGIBILITY BY ID
	@GetMapping("/loaneligibility/{id}")
	public ResponseEntity<String> checkLoanEligibility(@PathVariable("id") int id,
			@RequestParam("loanAmount") double loanAmount) {
		Optional<Account> optional = accountRepository.findById(id);
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid AccountId");
		}
		boolean eligible = accountService.loaneligibility(id, loanAmount);
		if (eligible) {
			return ResponseEntity.ok("Eligible for loan");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Eligible for loan.");
		}
	}

	// CALCULATE MONTHLY EMI BY ID
	@GetMapping("monthlyemi/{id}")
	public ResponseEntity<?> checkmonthlyemi(@PathVariable("id") int id, @RequestParam("loanAmount") double loanAmount,
			@RequestParam("month") int month) {
		// If the loan tenure is less than 6 months, we will not proceed further
		if (month < 6) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Less than SIX month are not allowed.");
		}
		if (loanAmount < 10000) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Less than 10000 are not allowed.");
		}
		Optional<Account> optional = accountRepository.findById(id);
		if (!optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid AccountId");
		}
		boolean eligible = accountService.loaneligibility(id, loanAmount);
		if (!eligible) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not for eligible for loan.");
		}
		double amount = accountService.emi(loanAmount, month);
		Loan loan = new Loan();
		loan.setLoanAmount(amount);
		loan.setMonth(month);
		return ResponseEntity.ok(loan);
	}
}
