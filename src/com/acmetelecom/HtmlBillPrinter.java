package com.acmetelecom;

/**
 * Responsible for printing parts of a bill in HTML form.
 */
public class HtmlBillPrinter implements IBillPrinter {

	/**
	 * Prints the bill heading.
	 * @param name The customer name to include in the heading.
	 * @param phoneNumber The customer's phone number.
	 * @param pricePlan The customer's price plan/tariff.
	 */
    public void printHeading(String name, String phoneNumber, String pricePlan) {
        beginHtml();
        System.out.println(h2(name + "/" + phoneNumber + " - " + "Price Plan: " + pricePlan));
        beginTable();
    }

    /**
     * Prints the specified call information as a HTML table entry.
     * @param time The start time of the call.
     * @param callee The receiver's phone number.
     * @param duration The call duration.
     * @param cost The call cost.
     */
    public void printItem(String time, String callee, String duration, String cost) {
        System.out.println(tr(td(time) + td(callee) + td(duration) + td(cost)));
    }
    
    /**
     * Prints the total cost at the bottom of the HTML bill.
     * @param total The bill total cost.
     */
    public void printTotal(String total) {
        endTable();
        System.out.println(h2("Total: " + total));
        endHtml();
    }
    
    /**
     * Begins a HTML table.
     */
    private void beginTable() {
        System.out.println("<table border=\"1\">");
        System.out.println(tr(th("Time") + th("Number") + th("Duration") + th("Cost")));
    }

    /**
     * Ends a HTML table.
     */
    private void endTable() {
        System.out.println("</table>");
    }

    /**
     * Creates a HTML header (level 2).
     * @param text The header text.
     * @return The header's HTML.
     */
    private String h2(String text) {
        return "<h2>" + text + "</h2>";
    }

    /**
     * Creates a HTML table row with the specified text.
     * @param text The text to include in the table row.
     * @return The HTML for the table row.
     */
    private String tr(String text) {
        return "<tr>" + text + "</tr>";
    }

    /**
     * Creates a HTML table header with the specified text.
     * @param text The text to include in the table header.
     * @return The HTML for the table header.
     */
    private String th(String text) {
        return "<th width=\"160\">" + text + "</th>";
    }

    /**
     * Creates a HTML table element with the specified text.
     * @param text The text to include in the table element.
     * @return The HTML for the table element.
     */
    private String td(String text) {
        return "<td>" + text + "</td>";
    }

    /**
     * Prints the HTML bill header.
     */
    private void beginHtml() {
        System.out.println("<html>");
        System.out.println("<head></head>");
        System.out.println("<body>");
        System.out.println("<h1>");
        System.out.println("Acme Telecom");
        System.out.println("</h1>");
    }

    /**
     * Ends the HTML bill.
     */
    private void endHtml() {
        System.out.println("</body>");
        System.out.println("</html>");
    }
}
