# Organizer (Pre-Alpha)

An android application to organize daily life modules. Currently, the application only manage "Money Manager" module.

## Install

- Download the apk file from the link: https://github.com/ColorlessCoder/Organizer/tree/master/app/release
- Install the app giving trusted source permission

## Money Manager

Soul purpose of this module is to keep track of daily expenses and balance.
### Account:
You can create Account to virtually split your money instead of real life account.
For example, one can create account "Wallet" to track his/her money in the wallet or bag. In the same way, one can create accounts "Travel", "Savings", "Medical" to distribute/categorize the money.
Benefit: One can have a clear idea of expendible money at some point and can reach to a goal of savings.
To create account, click ADD ACCOUNT button and fill up the account name, ammount and the background color for the account.

### Transaction & Categories:
After creating account, obviously there needs to be a way of transferring money, adding money to account and spend money. Clicking on an account will navigate to the account details and there will be a Transaction button. Clicking the button will initiate a transaction screen. Choose the type of transaction "Expense"/"Transfer"/"Income". Double click on the To account / From account will allow to select an existing account.

Besides amount, account and details, there is field named "Categroy" which is category of the transaction type. For example, one can spent money on Food, Dress etc. The same applies for income (salary, bonus etc). Unfortunately, there is currently no way to create a category in the time of transaction and there will be no pre-existing category and user needs to create them manually. The category needs to be created first. On the Money Manager screen, there is an icon button which will navigate one to category. Select a Transaction Type and click the + button at the bottom. Note that, the category will be created for the select transaction type and there is no way to select transaction type at the category creation screen.

### Transaction History
In the Money Manager screen, there are 4 icon buttons below the Account list, if long pressing each button will display the label. 3rd button from left is for transaction history. All the transactions will be visible there with filtering options. Clicking one transactions will show the details of tranasction and can be reverted if needed (except for some special transactions). Besides the filter icon there are two summary icons. One will show the summary as list of current search of transaction result and another one will show as chart.

### Transaction Plan
Purpose of transaction plan is to apply a bunch of predefined transactions at a single apply. The + icon at Transaction Plan header will navigate to creation screen (name and forground color). After creating a plan, now some transactions need to be defined for the plan. Clicking on the plan will display a popup with options Edit Plan Details, Template Transactions and Apply plan. Edit plan Details options will provide user ability to rename plan or change color. Template Transactions will navigate user to template screen where user can create template with the same approach of transaction creation.
And finally, the Apply Plan option will apply all the transactions to the accounts.
For example, every month a certain salary will come to account and one might want to split the amount in other accounts like Medical fee, Travel Savings etc. So user can create 3 templates, Income a certain amount, Transfer certain amount to Medical and so on. At the beginning of month, user can only apply the plan instead of creating 3 transaction manually. Currently there is no option to schedule plan apply(because salary arrival is not static).

### Debt
The 4th icon in Money Manager is for Debt and it will navigate the user to Debt screen. The debt can be categorized as Borrowed (user borrowed money from someone), Lent (User lent money to someone) and Installment (user have to pay for something for a period). Debt record creation is straightforward. Creation of borrowed record will add the money to the selected account. Creation of lent record will subtract the money from the selected account. Installment is not related to any account.
Selecting a debt record will provide some options below the row. + button will act as a payment (Borrowed - Expense, Lent - Income, Installment - Expense). There will be a history button to see the transactions that were done under that debt record. User can edit or delete Debt record. Complete button will complete the debt record even if full amount is not paid. Paying the full amount will automatically complete the record. Note that, currently application will only show the incompleted debt records. User can still see the transactions in main Transaction History screen.

### Charts
The 1st icon in Money Manager is for Charts. User can create chart and see the expense, transfer, income by date. Currently the chart points need to be created (super) manually.

## Backup
Before opening this module, the permission of read write files needs to be provided manually. Go to App Infor> Permissions> Accept the Files and Media.

The app is not storing the data online. For that reason, user have to manually backup the data. Click export button and select a folder, applicaiton will ask for a filename and the backup will be stored at that location. The same backup file can be imported with import option.
