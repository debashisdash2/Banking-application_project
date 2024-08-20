package com.example.bankingapplication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankingapplication.Account;
import com.example.bankingapplication.repo.AccountRepository;

@Service
public class AccountService {
	@Autowired
	private AccountRepository accountRepository;

	public boolean loaneligibility(int id, double loanAmount) {
		Optional<Account> optional = this.accountRepository.findById(id);
		if (optional.isPresent()) {
			Account account = optional.get();
			double bal = account.getAccBalance();
			double reqbal = loanAmount * 0.5;
			return bal > reqbal;
		}
		return false;
	}

	public double emi(double loanAmount, int month) {
		double annuallnterestRate = 10.0;
		double monthlyInterestRate = annuallnterestRate / 12 / 100;
		double emiamount = (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, month))
				/ (Math.pow(1 + monthlyInterestRate, month) - 1);
		return emiamount;
	}
}