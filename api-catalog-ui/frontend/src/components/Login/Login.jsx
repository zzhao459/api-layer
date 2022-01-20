import { Component } from 'react';
import { Button, FormField, TextInput, Card, CardTitle, CardBlock } from 'mineral-ui';
import { IconDanger } from 'mineral-ui-icons';

import logoImage from '../../assets/images/api-catalog-logo.png';
import './Login.css';
import './LoginWebflow.css';
import Spinner from '../Spinner/Spinner';

export default class Login extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
            errorMessage: '',
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.backToLogin = this.backToLogin.bind(this);
    }

    isDisabled = () => {
        const { isFetching } = this.props;
        return isFetching;
    };

    handleError = auth => {
        const { error, expired } = auth;
        let messageText;
        let invalidNewPassword;
        const { authentication } = this.props;
        // eslint-disable-next-line global-require
        const errorMessages = require('../../error-messages.json');
        if (
            error.messageNumber !== undefined &&
            error.messageNumber !== null &&
            error.messageType !== undefined &&
            error.messageType !== null
        ) {
            messageText = `Unexpected error, please try again later (${error.messageNumber})`;
            const filter = errorMessages.messages.filter(
                x => x.messageKey != null && x.messageKey === error.messageNumber
            );
            invalidNewPassword = error.messageNumber === 'ZWEAS198E';
            const isSuspended = error.messageNumber === 'ZWEAS197E';
            if (filter.length !== 0) messageText = `(${error.messageNumber}) ${filter[0].messageText}`;
            if (invalidNewPassword || isSuspended) {
                messageText = `(${error.messageNumber}) ${filter[0].messageText}`;
            }
        } else if (error.status === 401 && authentication.sessionOn) {
            messageText = `(${errorMessages.messages[0].messageKey}) ${errorMessages.messages[0].messageText}`;
            authentication.onCompleteHandling();
        } else if (error.status === 500) {
            messageText = `(${errorMessages.messages[1].messageKey}) ${errorMessages.messages[1].messageText}`;
        }
        return { messageText, expired, invalidNewPassword };
    };

    handleChange(e) {
        const { name, value } = e.target;
        this.setState({ [name]: value });
        if (name === 'repeatNewPassword') {
            const { newPassword } = this.state;
            const { validateInput } = this.props;
            validateInput({ newPassword, repeatNewPassword: value });
        }
    }

    backToLogin() {
        this.setState({ newPassword: null });
        this.setState({ repeatNewPassword: null });
        const { returnToLogin } = this.props;
        returnToLogin();
    }

    handleSubmit(e) {
        e.preventDefault();

        const { username, password, newPassword } = this.state;
        const { login } = this.props;
        if (username && password && newPassword) {
            login({ username, password, newPassword });
        } else if (username && password) {
            login({ username, password });
        }
    }

    render() {
        const { username, password, errorMessage, newPassword, repeatNewPassword } = this.state;
        const { authentication, isFetching } = this.props;
        let error = { messageText: null, expired: false, invalidNewPassword: true };
        if (
            authentication !== undefined &&
            authentication !== null &&
            authentication.error !== undefined &&
            authentication.error !== null
        ) {
            error = this.handleError(authentication);
            if (authentication.error.messageNumber === 'ZWEAS197E') {
                return (
                    <div className="login-object">
                        <div className="login-form">
                            <div className="susp-card">
                                <Card>
                                    <CardTitle>{error.messageText}</CardTitle>
                                    <CardBlock>
                                        {username} account has been suspended. Contact your security administrator to
                                        unsuspend your account.
                                    </CardBlock>
                                    <Button
                                        onClick={this.backToLogin}
                                        data-testid="backToLogin"
                                        primary
                                        fullWidth
                                        size="jumbo"
                                    >
                                        RETURN TO LOGIN
                                    </Button>
                                </Card>
                            </div>
                        </div>
                    </div>
                );
            }
        } else if (errorMessage) {
            error.messageText = errorMessage;
        }

        return (
            <div className="login-object">
                <div className="login-form">
                    {' '}
                    <div className="title-container">
                        <div className="logo-container">
                            <img src={logoImage} alt="" />
                        </div>
                        <div className="product-title">
                            <div className="text-block-2">API Catalog</div>
                        </div>
                    </div>
                    <div className="login-inputs-container">
                        <div className="username-container">
                            <div className="username-input">
                                <div className="w-form">
                                    <form
                                        id="login-form"
                                        name="login-form"
                                        data-testid="login-form"
                                        data-name="Login Form"
                                        className="form"
                                        onSubmit={this.handleSubmit}
                                    >
                                        {!error.expired && (
                                            <FormField label="Username" className="formfield">
                                                <TextInput
                                                    id="username"
                                                    data-testid="username"
                                                    name="username"
                                                    type="text"
                                                    size="jumbo"
                                                    value={username}
                                                    onChange={this.handleChange}
                                                    autocomplete
                                                />
                                            </FormField>
                                        )}
                                        {!error.expired && (
                                            <FormField label="Password" className="formfield">
                                                <TextInput
                                                    id="password"
                                                    data-testid="password"
                                                    name="password"
                                                    type="password"
                                                    size="jumbo"
                                                    value={password}
                                                    onChange={this.handleChange}
                                                    caption="Default: password"
                                                    autocomplete
                                                />
                                            </FormField>
                                        )}
                                        {!error.expired && (
                                            <FormField className="formfield" label="">
                                                <Button
                                                    type="submit"
                                                    data-testid="submit"
                                                    primary
                                                    fullWidth
                                                    disabled={this.isDisabled()}
                                                    size="jumbo"
                                                >
                                                    Sign in
                                                </Button>
                                            </FormField>
                                        )}
                                        {error.expired && (
                                            <FormField label="NewPassword" className="formfield">
                                                <TextInput
                                                    id="newPassword"
                                                    data-testid="newPassword"
                                                    name="newPassword"
                                                    type="password"
                                                    size="jumbo"
                                                    value={newPassword}
                                                    onChange={this.handleChange}
                                                    caption="Default: new password"
                                                    autocomplete
                                                />
                                            </FormField>
                                        )}
                                        {error.expired && (
                                            <FormField
                                                label="RepeatNewPassword"
                                                className="formfield"
                                                variant={error.invalidNewPassword ? 'danger' : ''}
                                            >
                                                <TextInput
                                                    id="repeatNewPassword"
                                                    data-testid="repeatNewPassword"
                                                    name="repeatNewPassword"
                                                    type="password"
                                                    size="jumbo"
                                                    value={repeatNewPassword}
                                                    onChange={this.handleChange}
                                                    caption="Default: Repeat new password"
                                                    autocomplete
                                                />
                                            </FormField>
                                        )}
                                        <div className="parent">
                                            {error.expired && (
                                                <FormField className="formfield" label="">
                                                    <Button
                                                        onClick={this.backToLogin}
                                                        data-testid="backToLogin"
                                                        primary
                                                        fullWidth
                                                        disabled={this.isDisabled()}
                                                        size="jumbo"
                                                    >
                                                        BACK
                                                    </Button>
                                                </FormField>
                                            )}
                                            {error.expired && (
                                                <FormField className="formfield" label="">
                                                    <Button
                                                        type="submit"
                                                        data-testid="submitChange"
                                                        primary
                                                        fullWidth
                                                        disabled={!repeatNewPassword || error.invalidNewPassword}
                                                        size="jumbo"
                                                    >
                                                        CHANGE PASSWORD
                                                    </Button>
                                                </FormField>
                                            )}
                                        </div>
                                        <FormField className="formfield form-spinner" label="">
                                            <Spinner
                                                isLoading={isFetching}
                                                css={{
                                                    position: 'relative',
                                                    top: '70px',
                                                }}
                                            />
                                        </FormField>
                                        {error.messageText !== undefined &&
                                            error.messageText !== null && (
                                                <FormField className="error-message" label="">
                                                    <div id="error-message">
                                                        <p className="error-message-content">
                                                            <IconDanger color="#de1b1b" size="2rem" />
                                                            {error.messageText}
                                                        </p>
                                                    </div>
                                                </FormField>
                                            )}
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}
