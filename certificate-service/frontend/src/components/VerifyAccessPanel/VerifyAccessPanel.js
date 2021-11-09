import React, {  useMemo } from "react";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import './VerifyAccessPanel.css'
import '../../assets/css/APIMReactToastify.css';
import { ToastContainer, toast } from 'react-toastify';

function VerifyAccessPanel() {

    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
    const [serviceUrl, setServiceUrl] = React.useState(null);
    const [certAlias, setCertAlias] = React.useState(null);
    const [errors, setErrors] =  React.useState(null);
    const [message, setMessage] =  React.useState(null);

    const onSubmit = async () => {
        await sleep(300);
        const url = process.env.REACT_APP_GATEWAY_URL + `/api/v1/certificate-service/certificate?label=${certAlias}&url=${serviceUrl}`;
        fetch(url, {
            method: 'POST',
            mode: 'cors'
        }).then((response) => {
            if (response.ok) {
                console.log(response);
                setMessage("Certificate added to the truststore!");
                toast.success(message, {
                    closeOnClick: true,
                    autoClose: 2000,
                });
                toast.dismiss();
            } else if (!response.ok) {
                throw Error(response.statusText);
            }
        }).catch((error) => {
            setErrors(`Couldn't add the certificate due to the following error: ${error.message}`);
            toast.error(errors, {
                closeOnClick: true,
                autoClose: 2000,
            });
            toast.dismiss();
            // setErrors(error.message)
        });
    };

    const onVerify = async () => {
        await sleep(300);
        const url = process.env.REACT_APP_GATEWAY_URL + `/api/v1/certificate-service/verify?url=${serviceUrl}`
        fetch(url, {
            method: 'GET',
            mode: 'cors'
        }).then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
            toast.success("The service is successfully accessible!", {
                closeOnClick: true,
                autoClose: 2000,
            });
            toast.dismiss();
        }).catch((error) => {
            setErrors(`Access verification failed due to the following error: ${error.message}`);
            toast.error(errors, {
                closeOnClick: true,
                autoClose: 2000,
            });
            toast.dismiss();
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
            <div>
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

                </form>
            </Styles>
                {/*{ message && (*/}
                    <ToastContainer />
                {/*)}*/}
            </div>
        );

}

export default VerifyAccessPanel;


