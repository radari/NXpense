package nxpense.service.api;

import nxpense.domain.Expense;
import nxpense.dto.BalanceInfoDTO;
import nxpense.dto.ExpenseDTO;
import nxpense.exception.RequestCannotCompleteException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 *
 */
public interface ExpenseService {


    /**
     * Converts the given expense DTO instance into an instance of {@link nxpense.domain.Expense} and persists it in the DB.
     * @param expenseDTO Expense representation sent by the client and that is to be persisted.
     * <br>
     * <b>NOTE</b>
     * This method is transactional and will rollback for any thrown instance of {@link java.lang.Exception}
     * @return DB-synchronized {@link nxpense.domain.Expense} object that has been created and persisted.
     */
    public Expense createNewExpense(ExpenseDTO expenseDTO);

    /**
     * Retrieves the page corresponding to the requested page number, with the specified page size and that contains
     * expenses that are sorted by the given attributes, in the specified direction.
     * @param pageNumber number of the result page to be returned
     * @param size number of result items per page
     * @param direction sorting direction: ASC/DESC
     * @param properties attributes by which results are sorted
     * @return {@link org.springframework.data.domain.Page} object containing requested expense items
     */
    public Page<Expense> getPageExpenses(Integer pageNumber, Integer size, Sort.Direction direction, String[] properties);

    /**
     * Updates the expense with the specified {@code id} with the information held in the {@code expenseDTO} object.
     * @param id ID of the expense item to be updated
     * @param expenseDTO Object containing the new state of the expense to be updated
     * @return The updated version of the expense item
     */
    public Expense updateExpense(int id, ExpenseDTO expenseDTO);

    /**
     * Deletes the {@link nxpense.domain.Expense} items whose ID is is the specified list of ID from the DB.
     * @param ids List of ID of the {@link nxpense.domain.Expense} items that must be deleted from DB
     * @throws RequestCannotCompleteException if the specified list of ID is null
     */
    public void deleteExpense(List<Integer> ids) throws RequestCannotCompleteException;

    /**
     * Retrieves the balance information related to the current user.
     * @return Object containing balance information such as the sum of verified expenses and the sum of non-verified expenses
     */
    public BalanceInfoDTO getBalanceInfo();

    /**
     * Associates the Tag identified by {@code tagId} to the expense identified by {@code expenseId}
     * @param expenseId ID of the expense to which the tag with ID = {@code tagId} is to be associated with
     * @param tagId ID of the tag to be associated with the target expense item
     * @throws nxpense.exception.BadRequestException if no expense found for {@code expenseId} or no tag found for {@code tagId}
     * @throws nxpense.exception.ForbiddenActionException if specified expense or specified tag does not belong to current user
     */
    public void associateTagToExpense(int expenseId, int tagId);
}
