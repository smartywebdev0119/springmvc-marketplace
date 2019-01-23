package com.trade.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * This service calculates number of pages to show in pagination for page
 */
public class PaginationService {

    private static final int MAX_PAGE_NUMBER_FOR_SHOWING_WITHOUT_A_GAP = 7;

    private final static int NUMBER_OF_PAGES_FOR_PAGINATION = 7;


    /**
     * THis method generates page numbers for pagination for displaying them on the page
     *
     * @param currentPage        is the number of current page
     * @param totalNumberOfPages is the total number of pages
     * @return list of Integers that contains first, last pages and NUMBER_OF_PAGES_FOR_PAGINATION
     * of pages after the current one
     */
    public List<Integer> calcPageNumbersForPagination(int currentPage, int totalNumberOfPages) {

        // if current page is 0 or negative number
        if (currentPage <= 0) {
            currentPage = 1;
        }

        // in this case we do not add a gap in page numbers
        if (totalNumberOfPages <= MAX_PAGE_NUMBER_FOR_SHOWING_WITHOUT_A_GAP) {

            return IntStream
                    .range(1, (MAX_PAGE_NUMBER_FOR_SHOWING_WITHOUT_A_GAP + 1))
                    .boxed()
                    .collect(Collectors.toList());
        }


        Set<Integer> pages2 = new LinkedHashSet<>();

        if ((currentPage + NUMBER_OF_PAGES_FOR_PAGINATION) > totalNumberOfPages) {

            pages2.add(1);

            for (int i = currentPage; i <= totalNumberOfPages; i++) {

                pages2.add(i);
            }

            pages2.add(totalNumberOfPages);

        } else {

            pages2.add(1);

            for (int i = currentPage; i <= (currentPage + NUMBER_OF_PAGES_FOR_PAGINATION); i++) {

                pages2.add(i);
            }

            pages2.add(totalNumberOfPages);
        }

        return new ArrayList<>(pages2);
    }


}
