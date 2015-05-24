package nxpense.controller;

import nxpense.domain.Expense;
import nxpense.dto.*;
import nxpense.helper.ExpenseConverter;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseDTO newExpenseDTO) {

        try {
            Expense createdExpense = expenseService.createNewExpense(newExpenseDTO);
            ExpenseResponseDTO responseDto = ExpenseConverter.entityToResponseDto(createdExpense);
            return new ResponseEntity<ExpenseResponseDTO>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Could not complete new expense creation", e);
            return new ResponseEntity<ExpenseResponseDTO>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<PageDTO<ExpenseResponseDTO>> listExpense(@RequestParam Integer page,
                                                        @RequestParam Integer size,
                                                        @RequestParam Sort.Direction direction,
                                                        @RequestParam String[] properties) {

        // NOTE: page number used by spring-data-jpa is zero-based!
        Page<Expense> expensePage = expenseService.getPageExpenses(page - 1, size, direction, properties);
        PageDTO<ExpenseResponseDTO> pageDto = ExpenseConverter.expensePageToExpensePageDto(expensePage);
        // NOTE: increment page number before sending response to client, as we had to pass a zero-based value to the expenseService
        pageDto.setPageNumber(pageDto.getPageNumber() + 1);

        return new ResponseEntity<PageDTO<ExpenseResponseDTO>>(pageDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{expenseId}", method = RequestMethod.PUT)
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable int expenseId, @RequestBody ExpenseDTO expenseDto) {
        Expense updatedExpense = expenseService.updateExpense(expenseId, expenseDto);
        return new ResponseEntity<ExpenseResponseDTO>(ExpenseConverter.entityToResponseDto(updatedExpense), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteExpense(@RequestParam List<Integer> ids)  {
        expenseService.deleteExpense(ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BalanceInfoDTO> getBalance() {
        BalanceInfoDTO balance = expenseService.getBalanceInfo();
        return new ResponseEntity<BalanceInfoDTO>(balance, HttpStatus.OK);
    }

    @RequestMapping(value = "/{expenseId}/tag", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<List<TagResponseDTO>> addTagToExpense(@PathVariable Integer expenseId, @RequestParam(required = true, value = "id") Integer tagId) {
        Expense updatedExpense = expenseService.associateTagToExpense(expenseId, tagId);
        ExpenseResponseDTO responseDto = ExpenseConverter.entityToResponseDto(updatedExpense);
        return new ResponseEntity<List<TagResponseDTO>>(responseDto.getTags(), HttpStatus.OK);
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> translateException() {
        String errorMsg = "The expense item you working on has been updated/deleted by another session. Please reload the expense details and retry...";
        return new ResponseEntity<String>(errorMsg, HttpStatus.CONFLICT);
    }
}
