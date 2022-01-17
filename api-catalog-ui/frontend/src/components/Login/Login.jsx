import { Component } from 'react';
import { Button, FormField, TextInput } from 'mineral-ui';
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
    }

    isDisabled = () => {
        const { isFetching } = this.props;
        return isFetching;
    };

    handleError = error => {
        let messageText;
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
            if (filter.length !== 0) messageText = `(${error.messageNumber}) ${filter[0].messageText}`;
            if (error.messageNumber === 'ZWEAS199E') {
                messageText = `(${error.messageNumber}) ${filter[0].messageText}`;
            }
        } else if (error.status === 401 && authentication.sessionOn) {
            messageText = `(${errorMessages.messages[0].messageKey}) ${errorMessages.messages[0].messageText}`;
            authentication.onCompleteHandling();
        } else if (error.status === 500) {
            messageText = `(${errorMessages.messages[1].messageKey}) ${errorMessages.messages[1].messageText}`;
        }
        return messageText;
    };

    handleChange(e) {
        const { name, value } = e.target;
        this.setState({ [name]: value });
    }

    handleSubmit(e) {
        e.preventDefault();

        const { username, password, newPassword } = this.state;
        const { login } = this.props;
        debugger;
        if (username && password) {
            login({ username, password });
        }
        if (username && password && newPassword) {
            login({ username, password, newPassword });
        }
    }

    render() {
        const { username, password, errorMessage, newPassword, repeatNewPassword } = this.state;
        const { authentication, isFetching } = this.props;
        let messageText;
        if (
            authentication !== undefined &&
            authentication !== null &&
            authentication.error !== undefined &&
            authentication.error !== null
        ) {
            messageText = this.handleError(authentication.error);
        } else if (errorMessage) {
            messageText = errorMessage;
        }
        debugger;
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
                                        {authentication === null ||
                                            (authentication.error.messageNumber !== 'ZWEAS199E' && (
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
                                            ))}
                                        {messageText !== 'helevole' && (
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
                                        {messageText !== undefined &&
                                            messageText === 'helevole' && (
                                                <FormField label="NewPassword" className="formfield">
                                                    <TextInput
                                                        id="new-password"
                                                        data-testid="new-password"
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
                                        {messageText !== undefined &&
                                            messageText === 'helevole' && (
                                                <FormField label="RepeatNewPassword" className="formfield">
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
                                        <FormField className="formfield form-spinner" label="">
                                            <Spinner
                                                isLoading={isFetching}
                                                css={{
                                                    position: 'relative',
                                                    top: '70px',
                                                }}
                                            />
                                        </FormField>
                                        {messageText !== undefined &&
                                            messageText !== null && (
                                                <FormField className="error-message" label="">
                                                    <div id="error-message">
                                                        <p className="error-message-content">
                                                            <IconDanger color="#de1b1b" size="2rem" /> {messageText}
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
