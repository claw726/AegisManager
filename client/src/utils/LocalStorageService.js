export default class LocalStorageService {
    constructor() {
        this.userAccounts = JSON.parse(localStorage.getItem('UserAccounts')) || [];
        this.organizations = JSON.parse(localStorage.getItem('Organizations')) || [];
    }

    saveUserAccount(user) {
        this.userAccounts.push(user);
        localStorage.setItem('UserAccounts', JSON.stringify(this.userAccounts));
    }

    getUserAccounts() {
        return this.userAccounts;
    }

    getUserByEmail(email) {
        return this.userAccounts.find(user => user.email === email);
    }

    saveOrganization(organizaiton) {
        this.organizations.push(organizaiton);
        localStorage.setItem('Organizations', JSON.stringify(this.organizations));
    }

    getOrganizations() {
        return this.organizations;
    }

    getOrganizationsByIndex(index) {
        return this.organizations[index];
    }

    getOrganizationByName(name) {
        return this.organizations.find(organization => organization.name === name);
    }

    getOrganizationByCreator(creator) {
        return this.organizations.find(organization => organization.creator === creator);
    }
}