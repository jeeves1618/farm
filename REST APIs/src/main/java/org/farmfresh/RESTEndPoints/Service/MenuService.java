package org.farmfresh.RESTEndPoints.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.farmfresh.RESTEndPoints.Domain.UIMetaData;
import org.farmfresh.RESTEndPoints.Entity.Menu;
import org.farmfresh.RESTEndPoints.Repo.MenuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Data
@Slf4j
public class MenuService {

    @Autowired
    Menu menu;

    @Autowired
    MenuRepo menuRepo;

    DecimalFormat ft = new DecimalFormat("Rs ##,##,##0.00");
    RupeeFormatter rf = new RupeeFormatter();
    ArrayList<Menu> menuList = new ArrayList<Menu>();
    String accountNumber = null;

    public List<Menu> getEntries(String fileWithPathname, UIMetaData uiMetaData) {
        switch (uiMetaData.getTypeOfStatement()) {
            case "M":
            case "Menu":
                return getAccountEntries(fileWithPathname);
            case "I":
            case "Inventory":
                return null;
            default:
                return null;
        }
    }

    public List<Menu> getAccountEntries(String fileWithPathname) {
        int bsIterator = 0;
        try {
            FileInputStream file = new FileInputStream(new File(fileWithPathname));
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            //Read through the Header Rows

            Row headerRow = rowIterator.next();
            boolean skipProcessing = true, moreTransactions = true;
            while (rowIterator.hasNext() && skipProcessing) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell = cellIterator.next();
                cell = cellIterator.next();
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().contains("Transactions List")) {
                    String accountString = cell.getStringCellValue();
                    accountNumber = accountString.substring(accountString.length() - 12);
                    log.info("accountString : " + accountString.substring(accountString.length() - 12));
                }
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().contains("S No.")) {
                    skipProcessing = false;
                }
            }

            while (rowIterator.hasNext() && moreTransactions) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                //Instantiate an Object for each individual member of Array
                //menu = new menu();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    //Check the cell type and format accordingly
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            //System.out.print(cell.getNumericCellValue() + "t");
                            switch (cell.getColumnIndex()) {
                                case 0:
                                    menu.setMenuItemId((int) cell.getNumericCellValue());
                                    //System.out.print(menu.cashValue + "t");
                                    break;
                                case 3:
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected NUMERIC Cell Value in the Spreadsheet :" + bsIterator + " and " + cell.getColumnIndex() + " Value = " + cell.getNumericCellValue());
                            }
                            break;
                        case STRING:
                            //System.out.print(cell.getStringCellValue() + "t");
                            switch (cell.getColumnIndex()) {
                                case 1:
                                    menu.setMenuItemName(cell.getStringCellValue());
                                    //System.out.print(menu.typeAssetOrLiability + "t");
                                    break;
                                case 2:
                                    menu.setMenuItemCategory(cell.getStringCellValue());
                                    //System.out.print(menu.subType + "t");
                                    break;
                                case 4:
                                    menu.setMenuItemDescription(cell.getStringCellValue());
                                    //System.out.print(menu.itemDescription + "t");
                                    break;
                                case 5:
                                    menu.setMenuTodaySpecialInd(cell.getStringCellValue());
                                    //System.out.print(menu.itemDescription + "t");
                                    break;
                                case 6:
                                    menu.setMenuBestSellerInd(cell.getStringCellValue());
                                    //System.out.print(menu.itemDescription + "t");
                                    break;
                                case 7:
                                    menu.setMenuImageFileName(cell.getStringCellValue());
                                    //System.out.print(menu.itemDescription + "t");
                                    break;
                                case 8:
                                    menu.setMenuAvailabilityInd(cell.getStringCellValue());
                                    //System.out.print(menu.itemDescription + "t");
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected STRING Cell Value in the Spreadsheet :" + bsIterator + " and " + cell.getColumnIndex());
                            }
                            break;
                        case BLANK:
                            //System.out.print(cell.getStringCellValue() + "t");
                            break;
                        default:
                            throw new IllegalStateException("Unexpected Cell Value in the Spreadsheet :" + bsIterator + " and " + cell.getColumnIndex());
                    }
                }
                 menuList.add(menu);
                menu = new Menu();
                bsIterator++;
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuList;
    }

    public void saveAccountEntries(List<Menu> menus) {
        menuRepo.saveAll(menus);
    }

    public List<Menu> findAll() {
        return menuRepo.findAll();
    }

    public Menu getById(Integer theId){
        return menuRepo.getById(theId);
    }

    public List<Menu> findAvailableItems(String menuAvailabilityInd){
        return menuRepo.findByMenuAvailabilityInd(menuAvailabilityInd);
    }

    public void save(Menu menu){
        menuRepo.save(menu);
    }
}

