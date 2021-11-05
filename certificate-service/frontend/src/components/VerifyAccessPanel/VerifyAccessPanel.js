import React, {  useMemo } from "react";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import './VerifyAccessPanel.css'

function VerifyAccessPanel() {

    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
    const [serviceUrl, setServiceUrl] = React.useState(null);
    const [certAlias, setCertAlias] = React.useState(null);
    const [errors, setErrors] =  React.useState(null);
    const onSubmit = async () => {
        await sleep(300);
        const url = process.env.REACT_APP_GATEWAY_URL + `/api/v1/certificate-service/certificate?label=${certAlias}&url=${serviceUrl}`;
        fetch(url, {
            method: 'POST',
            mode: 'cors'
        }).then((response) => {
            if (response.ok) {
                console.log(response);
                alert("Certificate added to the truststore!");
            } else if (!response.ok) {
                throw Error(response.statusText);
            }
        }).catch((error) => {
            console.log(error)
            alert(error.message);
            // setErrors(error.message)
        });
    };

    const onVerify = async () => {
        await sleep(300);
        // TODO use the below commented URL to route through gateway
        const url = process.env.REACT_APP_GATEWAY_URL + `/api/v1/certificate-service/verify?url=${serviceUrl}`
        fetch(url, {
            method: 'GET',
            mode: 'cors'
        }).then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
        }).catch((error) => {
            console.log(error)
            setErrors(error.message);
        });
    };

    const validate = values => {
        const errors = {};

        setServiceUrl(values.url);
        setCertAlias(values.alias);

        if (!values.url) {
            errors.url = "Required";
        }

        if (!values.alias) {
            errors.alias = "Required";
        }

        return errors;
    };

    const initialValues = useMemo(
            () => ({
                url: "",
                alias: "",
            }),
            []
    );

        const {form, handleSubmit, pristine, submitting} = useForm({
            onSubmit,
            initialValues,
            validate
        });

        const url = useField("url", form);
        const alias = useField("alias", form);

        return (
            <Styles>
                <h2> Verify access panel</h2>
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Hostname</label>
                        <input {...url.input} placeholder="Service Hostname"/>
                        {url.meta.touched && url.meta.error && (
                            <span>{url.meta.error}</span>
                        )}
                    </div>
                    <div>
                        <label disabled={errors == null}>Alias</label>
                        <input disabled={errors == null} {...alias.input} placeholder="Certificate Alias"/>
                        {alias.meta.touched && alias.meta.error && (
                            <span>{alias.meta.error}</span>
                        )}
                    </div>
                    <div className="buttons">
                        <Stack direction="row" alignItems="center" spacing={2}>
                            <label htmlFor="contained-button-file">
                                <Button  style={{textTransform: 'none'}} onClick={onVerify} variant="contained" component="span">
                                    Verify Service
                                </Button>
                            </label>
                        </Stack>
                        <button type="submit" disabled={submitting || errors == null}>
                            Add certificate
                        </button>
                        <button
                            type="button"
                            onClick={() => form.reset()}
                            disabled={submitting || pristine}
                        >
                            Reset
                        </button>
                    </div>
                    { errors && (
                        <pre>
                       {errors}
                        <br/>
                        </pre>
                    )}

                </form>
            </Styles>
        );

}

export default VerifyAccessPanel;


