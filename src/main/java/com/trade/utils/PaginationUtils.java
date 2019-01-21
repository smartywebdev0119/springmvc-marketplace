package com.trade.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationUtils {

    private static final int MAX_PAGE_NUMBER_FOR_SHOWING_WITHOUT_A_GAP = 10;

    private final static int NUMBER_OF_PAGES = 10;


    /**
     * THis method generates page numbers for pagination for displaying them on the page
     *
     * @param currentPage        is the number of current page
     * @param totalNumberOfPages is the whole number of pages
     * @return list of Integers where one element might me null.
     * Null element represents a gap between page numbers
     */
    public static List<Integer> calcPageNumbersForPagination(int currentPage, int totalNumberOfPages) {

        // in this case we do not add a gap in page numbers
        if (totalNumberOfPages <= MAX_PAGE_NUMBER_FOR_SHOWING_WITHOUT_A_GAP) {

            return IntStream
                    .range(1, (MAX_PAGE_NUMBER_FOR_SHOWING_WITHOUT_A_GAP + 1))
                    .boxed()
                    .collect(Collectors.toList());
        }



        // todo splitting of pages
//        << [1] 2 3 ... 99 100 >>
//        if total numb of pages is 10 or lower then show all pages
//        if total numb of pages is 11 and more then show first 10 and
//        hide others

        Set<Integer> pagesSys = new LinkedHashSet<>();

        if (totalNumberOfPages - currentPage > NUMBER_OF_PAGES){

            List<Integer> listWithPages = IntStream
                    .range(currentPage, (totalNumberOfPages + 1))
                    .boxed()
                    .collect(Collectors.toList());

            pagesSys.addAll(listWithPages);

            pagesSys.add(totalNumberOfPages);

        } else {

            int remainingPages = totalNumberOfPages - currentPage;

            List<Integer> listWithPages = IntStream
                    .range(currentPage, remainingPages + 1)
                    .boxed()
                    .collect(Collectors.toList());

            pagesSys.addAll(listWithPages);

        }



        // just generate sequence of numbers WITHOUT A GAP
        List<Integer> pages = IntStream
                .range(1, totalNumberOfPages)
                .boxed()
                .collect(Collectors.toList());

        return pages;

    }

}
