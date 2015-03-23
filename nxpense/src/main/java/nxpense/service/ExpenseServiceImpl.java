package nxpense.service;

import nxpense.domain.Expense;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.exception.BadRequestException;
import nxpense.exception.UnauthenticatedException;
import nxpense.helper.ExpenseConverter;
import nxpense.repository.ExpenseRepository;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetails;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("expenseService")
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional(rollbackFor = {Exception.class})
    public Expense createNewExpense(ExpenseDTO expenseDTO) {
        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        if (expense == null) {
            LOGGER.warn("Attempt to create a NULL expense!");
            throw new BadRequestException("Cannot persist a NULL expense entity");
        }

        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (currentUser == null) {
            throw new UnauthenticatedException("User does not seem to be authenticated!");
        }

        // First, call save( ) on existing user, to merge the detached User instance associated to security context...
        currentUser = userRepository.save(currentUser);

        int newExpensePosition = (int) expenseRepository.getNumberOfItemBeforeDate(currentUser, expense.getDate()) + 1;
        LOGGER.debug("Position of new expense with date {} for user with email {} = {}", expense.getDate(), currentUser.getEmail(), newExpensePosition);

        int numberOfUpdatedExpense = expenseRepository.incrementPosition(currentUser, newExpensePosition);
        LOGGER.debug("Incremented {} expense items' position field", numberOfUpdatedExpense);

        expense.setPosition(newExpensePosition);
        currentUser.addExpense(expense);
        expense.setUser(currentUser);

        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Page<Expense> getPageExpenses(Integer pageNumber, Integer size, Sort.Direction direction, String[] properties) {
        PageRequest pageRequest = new PageRequest(pageNumber, size, direction, properties);
        return expenseRepository.findAll(pageRequest);
    }
}
